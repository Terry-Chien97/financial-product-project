<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { Modal } from "bootstrap";
import DataTable, { Column } from "datatables.net-vue3";
import DataTablesCore from "datatables.net-bs5";
import { getProducts } from "@/services/productService.js";
import { getProfile } from "@/services/memberService.js";
import {
  getLikes,
  addLike,
  updateLike,
  deleteLike,
} from "@/services/likeListService.js";
DataTable.use(DataTablesCore);
const tableRef = ref(null);
const likeModalRef = ref(null);
const searchText = ref("");
const likes = ref([]);
const products = ref([]);
const likesLoaded = ref(false);
const productsLoaded = ref(false);
const isEdit = ref(false);
const defaultAccount = ref("");
const useDefaultAccount = ref(false);
const manualAccount = ref("");
const form = reactive({
  sn: null,
  productNo: "",
  purchaseQuantity: "",
  account: "",
});
const errors = reactive({
  productNo: "",
  purchaseQuantity: "",
  account: "",
});
const totalFee = computed(() =>
  likes.value.reduce((sum, item) => sum + Number(item.totalFee || 0), 0),
);
const totalAmount = computed(() =>
  likes.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0),
);
const formatMoney = (value) =>
  Number(value).toLocaleString("zh-TW", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
const availableProducts = computed(() =>
  products.value.filter(
    (product) =>
      !likes.value.some(
        (like) =>
          Number(like.productNo) === Number(product.no) && like.sn !== form.sn,
      ),
  ),
);
const options = {
  pageLength: 10,
  lengthMenu: [10, 25, 50],
  order: [[7, "desc"]],
  autoWidth: false,
  rowCallback: (row, data, i, index) => {
    row.cells[0].textContent = index + 1;
  },
  dom:
    "t" +
    "<'row align-items-center mt-2'" +
    "<'col-4'i>" +
    "<'col-4 d-flex justify-content-center'p>" +
    "<'col-4 d-flex justify-content-end'l>" +
    ">",
  language: {
    info: "共 _TOTAL_ 筆",
    infoEmpty: "共 0 筆",
    emptyTable: "尚無喜好資料",
    zeroRecords: "查無符合資料",
    lengthMenu: "每頁 _MENU_ 筆",
    paginate: {
      previous: "上一頁",
      next: "下一頁",
    },
  },
};
const preventInvalidNumberKey = (event) => {
  if (["e", "E", "+", "-", "."].includes(event.key)) {
    event.preventDefault();
  }
};
const validateField = (field) => {
  const value = String(form[field] ?? "").trim();
  if (field === "productNo") {
    if (!value) return "請選擇商品";
    if (!availableProducts.value.some((p) => p.no === Number(value))) {
      return "請選擇有效的商品";
    }
  }
  if (field === "purchaseQuantity") {
    if (!value) return "請輸入購買數量";
    if (!/^\d+$/.test(value)) return "購買數量僅允許整數";
    if (Number(value) < 1 || Number(value) > 2147483647) {
      return "購買數量須介於 1～2,147,483,647";
    }
  }
  if (field === "account") {
    if (!/^\d{10,16}$/.test(value)) {
      return "扣款帳號須為 10～16 碼數字";
    }
  }
  return "";
};
const checkField = (field) => {
  errors[field] = validateField(field);
};
const validateForm = () => {
  Object.keys(errors).forEach(checkField);
  return errors.productNo || errors.purchaseQuantity || errors.account || null;
};
const resetErrors = () => {
  Object.keys(errors).forEach((field) => {
    errors[field] = "";
  });
};
const handleAccountInput = (event) => {
  form.account = event.target.value.replace(/\D/g, "");
  manualAccount.value = form.account;
  checkField("account");
};
const loadDefaultAccount = async () => {
  defaultAccount.value = "";
  try {
    const response = await getProfile();
    const account = String(response.data?.account ?? "");
    if (/^\d{10,16}$/.test(account)) {
      defaultAccount.value = account;
    }
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const toggleDefaultAccount = () => {
  form.account = useDefaultAccount.value
    ? defaultAccount.value
    : manualAccount.value;
  errors.account = "";
};
const loadLikes = async () => {
  likesLoaded.value = false;
  try {
    const response = await getLikes();
    if (!Array.isArray(response.data)) {
      throw new Error("喜好清單格式錯誤");
    }
    likes.value = response.data;
    likesLoaded.value = true;
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const loadProducts = async () => {
  productsLoaded.value = false;
  try {
    const response = await getProducts();
    if (!Array.isArray(response.data)) {
      throw new Error("產品資料格式錯誤");
    }
    products.value = response.data;
    productsLoaded.value = true;
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const loadData = async () => {
  await Promise.all([loadLikes(), loadProducts()]);
};
const searchLike = () => {
  tableRef.value?.dt?.search(searchText.value).draw();
};
const openAddModal = async () => {
  if (!productsLoaded.value || !likesLoaded.value) {
    window.alertUI.toast("error", "資料尚未成功載入");
    return;
  }
  if (!products.value.length) {
    window.alertUI.toast("warning", "請先新增產品");
    return;
  }
  if (
    products.value.every((product) =>
      likes.value.some((like) => Number(like.productNo) === Number(product.no)),
    )
  ) {
    window.alertUI.toast("warning", "所有產品皆已加入喜好清單");
    return;
  }
  isEdit.value = false;
  form.sn = null;
  form.productNo = "";
  form.purchaseQuantity = "";
  form.account = "";
  manualAccount.value = "";
  await loadDefaultAccount();
  useDefaultAccount.value = !!defaultAccount.value;
  if (useDefaultAccount.value) form.account = defaultAccount.value;
  resetErrors();
  Modal.getOrCreateInstance(likeModalRef.value).show();
};
const openEditModal = async (like) => {
  isEdit.value = true;
  form.sn = like.sn;
  form.productNo = like.productNo;
  form.purchaseQuantity = String(like.purchaseQuantity);
  form.account = like.account;
  manualAccount.value = like.account;
  await loadDefaultAccount();
  useDefaultAccount.value =
    !!defaultAccount.value && form.account === defaultAccount.value;
  resetErrors();
  Modal.getOrCreateInstance(likeModalRef.value).show();
};
const saveLike = async () => {
  const errorMessage = validateForm();
  if (errorMessage) {
    window.alertUI.toast("error", errorMessage);
    return;
  }
  const data = {
    productNo: Number(form.productNo),
    purchaseQuantity: Number(form.purchaseQuantity),
    account: form.account.trim(),
  };
  try {
    const response = isEdit.value
      ? await updateLike(form.sn, data)
      : await addLike(data);
    Modal.getInstance(likeModalRef.value)?.hide();
    await loadLikes();
    window.alertUI.toast("success", response.data.message);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const removeLike = async (like) => {
  if (!(await window.alertUI.confirmDelete(like.productName))) return;
  try {
    const response = await deleteLike(like.sn);
    await loadLikes();
    window.alertUI.toast("success", response.data.message);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
onMounted(loadData);
</script>
<template>
  <div class="container-fluid py-3 px-3 px-lg-4">
    <div class="row g-2 mb-2">
      <div class="col-12 col-md-6">
        <div class="card shadow-sm">
          <div
            class="card-body d-flex align-items-center justify-content-between gap-2 py-2 px-3"
          >
            <span class="text-secondary small">預計扣款總金額</span>
            <span class="fw-bold text-end">
              {{ likesLoaded ? `TWD ${formatMoney(totalAmount)}` : "—" }}
            </span>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6">
        <div class="card shadow-sm">
          <div
            class="card-body d-flex align-items-center justify-content-between gap-2 py-2 px-3"
          >
            <span class="text-secondary small">總手續費</span>
            <span class="fw-bold text-end">
              {{ likesLoaded ? `TWD ${formatMoney(totalFee)}` : "—" }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="card shadow-sm">
      <div class="card-body p-3">
        <div class="row g-2 align-items-center mb-2">
          <div class="col-12 col-md-3 col-lg-2">
            <h4 class="fw-bold mb-0">喜好清單</h4>
          </div>
          <div class="col-12 col-md-6 col-lg-8">
            <div class="input-group">
              <span class="input-group-text bg-white">
                <i class="bi bi-search"></i>
              </span>
              <input
                v-model="searchText"
                type="text"
                class="form-control"
                placeholder="搜尋商品"
                @input="searchLike"
              />
            </div>
          </div>
          <div class="col-12 col-md-3 col-lg-2 text-md-end">
            <button type="button" class="btn btn-success" @click="openAddModal">
              <i class="bi bi-plus-lg me-1"></i>
              新增
            </button>
          </div>
        </div>
        <div class="table-responsive">
          <DataTable
            ref="tableRef"
            :data="likes"
            :options="options"
            class="table table-hover table-sm align-middle"
            style="width: 100%"
          >
            <Column
              title="序號"
              :data="null"
              :options="{
                defaultContent: '',
                orderable: false,
                searchable: false,
                width: '5%',
              }"
            />
            <Column
              title="商品名稱"
              data="productName"
              class-name="text-break"
              :options="{
                width: '17%',
                render: DataTablesCore.render.text(),
              }"
            />
            <Column
              title="價格"
              data="price"
              class-name="text-end text-nowrap"
              :options="{ width: '10%' }"
            >
              <template #default="{ cellData }">
                {{ formatMoney(cellData) }}
              </template>
            </Column>
            <Column
              title="手續費率"
              data="feeRate"
              class-name="text-end text-nowrap"
              :options="{ width: '9%' }"
            >
              <template #default="{ cellData }">
                {{ (Number(cellData) * 100).toFixed(4) }}%
              </template>
            </Column>
            <Column
              title="購買數量"
              data="purchaseQuantity"
              class-name="text-end text-nowrap"
              :options="{ width: '8%' }"
            >
              <template #default="{ cellData }">
                {{ Number(cellData).toLocaleString("zh-TW") }}
              </template>
            </Column>
            <Column
              title="扣款帳號"
              data="account"
              class-name="text-nowrap"
              :options="{
                width: '13%',
                render: DataTablesCore.render.text(),
              }"
            />
            <Column
              title="總手續費"
              data="totalFee"
              class-name="text-end text-nowrap"
              :options="{ width: '10%' }"
            >
              <template #default="{ cellData }">
                {{ formatMoney(cellData) }}
              </template>
            </Column>
            <Column
              title="預計扣款總金額"
              data="totalAmount"
              class-name="text-end text-nowrap"
              :options="{ width: '13%' }"
            >
              <template #default="{ cellData }">
                {{ formatMoney(cellData) }}
              </template>
            </Column>
            <Column
              title="操作"
              :options="{
                orderable: false,
                searchable: false,
                className: 'text-center',
                width: '15%',
              }"
            >
              <template #default="{ rowData }">
                <div class="d-flex flex-wrap justify-content-center gap-2">
                  <button
                    type="button"
                    class="btn btn-outline-success btn-sm text-nowrap"
                    @click="openEditModal(rowData)"
                  >
                    <i class="bi bi-pencil-square me-1"></i>
                    修改
                  </button>
                  <button
                    type="button"
                    class="btn btn-outline-danger btn-sm text-nowrap"
                    @click="removeLike(rowData)"
                  >
                    <i class="bi bi-trash me-1"></i>
                    刪除
                  </button>
                </div>
              </template>
            </Column>
          </DataTable>
        </div>
      </div>
    </div>
  </div>
  <div ref="likeModalRef" class="modal fade" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEdit ? "修改喜好" : "新增喜好" }}
          </h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
          ></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">商品名稱</label>
            <select
              v-model.number="form.productNo"
              class="form-select"
              :class="{ 'is-invalid': !!errors.productNo }"
              @change="checkField('productNo')"
              required
            >
              <option value="">請選擇商品</option>
              <option
                v-for="product in availableProducts"
                :key="product.no"
                :value="product.no"
                availableProduct
              >
                {{ product.productName }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label">購買數量</label>
            <input
              v-model="form.purchaseQuantity"
              type="number"
              class="form-control"
              :class="{ 'is-invalid': !!errors.purchaseQuantity }"
              placeholder="例如 10"
              required
              @keydown="preventInvalidNumberKey"
              @input="checkField('purchaseQuantity')"
            />
          </div>
          <div class="mb-3">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <label class="form-label mb-0">扣款帳號</label>
              <div class="form-check mb-0">
                <input
                  id="useDefaultAccount"
                  v-model="useDefaultAccount"
                  type="checkbox"
                  class="form-check-input"
                  :disabled="!defaultAccount"
                  @change="toggleDefaultAccount"
                />
                <label class="form-check-label" for="useDefaultAccount">
                  使用預設帳號
                </label>
              </div>
            </div>
            <input
              v-model="form.account"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': !!errors.account }"
              :disabled="useDefaultAccount"
              inputmode="numeric"
              placeholder="請輸入 10～16 碼扣款帳號"
              required
              @input="handleAccountInput"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            取消
          </button>
          <button type="button" class="btn btn-success" @click="saveLike">
            儲存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
