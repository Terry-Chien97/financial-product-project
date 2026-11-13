<script setup>
import { onMounted, reactive, ref } from "vue";
import { Modal } from "bootstrap";
import DataTable, { Column } from "datatables.net-vue3";
import DataTablesCore from "datatables.net-bs5";
import {
  getProducts,
  addProduct,
  updateProduct,
  deleteProduct,
} from "@/services/productService.js";
import { getLikes } from "@/services/likeListService.js";
DataTable.use(DataTablesCore);
const tableRef = ref(null);
const productModalRef = ref(null);
const searchText = ref("");
const products = ref([]);
const isEdit = ref(false);
const form = reactive({
  no: null,
  productName: "",
  price: "",
  feeRate: "",
});
const errors = reactive({
  productName: "",
  price: "",
  feeRate: "",
});
const options = {
  pageLength: 10,
  lengthMenu: [10, 25, 50],
  order: [[2, "desc"]],
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
    emptyTable: "尚無產品資料",
    zeroRecords: "查無符合資料",
    lengthMenu: "每頁 _MENU_ 筆",
    paginate: {
      previous: "上一頁",
      next: "下一頁",
    },
  },
};
const preventInvalidNumberKey = (event) => {
  if (["e", "E", "+", "-"].includes(event.key)) {
    event.preventDefault();
  }
};
const validateField = (field) => {
  const value = String(form[field] ?? "").trim();
  if (field === "productName") {
    if (!value) return "請輸入商品名稱";
    if (value.length > 50) return "商品名稱最多 50 字";
  }
  if (field === "price") {
    if (!value) return "請輸入價格";
    if (!/^\d+(\.\d{0,2})?$/.test(value)) {
      return "價格最多 2 位小數";
    }
    if (Number(value) < 0.01 || Number(value) > 99999999.99) {
      return "價格須介於 0.01～99,999,999.99";
    }
  }
  if (field === "feeRate") {
    if (!value) return "請輸入手續費率";
    if (!/^\d+(\.\d{0,4})?$/.test(value)) {
      return "手續費率最多 4 位小數";
    }
    if (Number(value) < 0 || Number(value) > 100) {
      return "手續費率須介於 0～100%";
    }
  }
  return "";
};
const checkField = (field) => {
  errors[field] = validateField(field);
};
const handleNumberInput = (field, event) => {
  form[field] = event.target.value;
  checkField(field);
};
const validateForm = () => {
  Object.keys(errors).forEach(checkField);
  return errors.productName || errors.price || errors.feeRate || null;
};
const resetErrors = () => {
  Object.keys(errors).forEach((field) => {
    errors[field] = "";
  });
};
const loadProducts = async () => {
  try {
    const response = await getProducts();
    products.value = response.data;
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const searchProduct = () => {
  tableRef.value?.dt?.search(searchText.value).draw();
};
const openAddModal = () => {
  isEdit.value = false;
  form.no = null;
  form.productName = "";
  form.price = "";
  form.feeRate = "";
  resetErrors();
  Modal.getOrCreateInstance(productModalRef.value).show();
};
const openEditModal = (product) => {
  isEdit.value = true;
  form.no = product.no;
  form.productName = product.productName;
  form.price = String(product.price ?? "");
  form.feeRate =
    product.feeRate == null
      ? ""
      : String(Number((Number(product.feeRate) * 100).toFixed(4)));
  resetErrors();
  Modal.getOrCreateInstance(productModalRef.value).show();
};
const saveProduct = async () => {
  const errorMessage = validateForm();
  if (errorMessage) {
    window.alertUI.toast("error", errorMessage);
    return;
  }
  const data = {
    productName: form.productName.trim(),
    price: Number(form.price),
    feeRate: Number((Number(form.feeRate) / 100).toFixed(6)),
  };
  try {
    const response = isEdit.value
      ? await updateProduct(form.no, data)
      : await addProduct(data);
    Modal.getInstance(productModalRef.value)?.hide();
    await loadProducts();
    window.alertUI.toast("success", response.data.message);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const removeProduct = async (product) => {
  try {
    const response = await getLikes();
    const isLiked = response.data.some(
      (like) => Number(like.productNo) === Number(product.no),
    );
    if (isLiked) {
      window.alertUI.toast("warning", "此產品已加入喜好清單，請先移除喜好紀錄");
      return;
    }
  } catch (error) {
    window.alertUI.apiError(error);
    return;
  }
  if (!(await window.alertUI.confirmDelete(product.productName))) return;
  try {
    const response = await deleteProduct(product.no);
    await loadProducts();
    window.alertUI.toast("success", response.data.message);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
onMounted(loadProducts);
</script>
<template>
  <div class="container-fluid py-3 px-3 px-lg-4">
    <div class="card shadow-sm">
      <div class="card-body p-3">
        <div class="row g-2 align-items-center mb-2">
          <div class="col-12 col-md-3 col-lg-2">
            <h4 class="fw-bold mb-0">產品清單</h4>
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
                placeholder="搜尋產品"
                @input="searchProduct"
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
            :data="products"
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
                width: '7%',
              }"
            />
            <Column
              title="商品名稱"
              data="productName"
              class-name="text-break"
              :options="{
                width: '39%',
                render: DataTablesCore.render.text(),
              }"
            />
            <Column
              title="價格"
              data="price"
              class-name="text-end text-nowrap"
              :options="{ width: '18%' }"
            >
              <template #default="{ cellData }">
                {{
                  Number(cellData).toLocaleString("zh-TW", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })
                }}
              </template>
            </Column>
            <Column
              title="手續費率"
              data="feeRate"
              class-name="text-end text-nowrap"
              :options="{ width: '16%' }"
            >
              <template #default="{ cellData }">
                {{ (Number(cellData) * 100).toFixed(4) }}%
              </template>
            </Column>
            <Column
              title="操作"
              :options="{
                orderable: false,
                searchable: false,
                className: 'text-center',
                width: '20%',
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
                    @click="removeProduct(rowData)"
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
  <div ref="productModalRef" class="modal fade" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEdit ? "修改產品" : "新增產品" }}
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
            <input
              v-model="form.productName"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': !!errors.productName }"
              placeholder="請輸入商品名稱"
              required
              @input="checkField('productName')"
            />
          </div>
          <div class="mb-3">
            <label class="form-label">價格</label>
            <input
              :value="form.price"
              type="number"
              class="form-control"
              :class="{ 'is-invalid': !!errors.price }"
              placeholder="例如 1500.00"
              required
              @keydown="preventInvalidNumberKey"
              @input="handleNumberInput('price', $event)"
            />
          </div>
          <div class="mb-3">
            <label class="form-label">手續費率 (%)</label>
            <input
              :value="form.feeRate"
              type="number"
              class="form-control"
              :class="{ 'is-invalid': !!errors.feeRate }"
              placeholder="例如 0.1425"
              required
              @keydown="preventInvalidNumberKey"
              @input="handleNumberInput('feeRate', $event)"
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
          <button type="button" class="btn btn-success" @click="saveProduct">
            儲存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
