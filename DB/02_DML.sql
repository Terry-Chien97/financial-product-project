USE FinancialProduct;
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;
    -- 1. 使用者資料
    -- 帳號：A123456789
    -- 密碼：123456
    INSERT INTO MEMBER.USER_ACCOUNT (
        USER_ID,
        USER_NAME,
        EMAIL,
        ACCOUNT,
        PASSWORD_HASH
    )
    VALUES (
        'A123456789',
        N'陳ＯＯ',
        'chen@text.com',
        '012345678901',
        '$2a$10$W7IogKdMtG6vb96d9GrT7eQE4khCBEdkYwSdB.cdxVMczT.lIt4t2'
    );
    -- 2. 金融商品資料
    INSERT INTO FINANCIAL.PRODUCT (
        USER_ID,
        PRODUCT_NAME,
        PRICE,
        FEE_RATE
    )
    VALUES
        ('A123456789', N'玉山中小型股基金－A類型',           278.16, 0.027500),
        ('A123456789', N'玉山高成長基金－TISA類型',           24.19, 0.000000),
        ('A123456789', N'玉山貨幣市場基金',                   16.96, 0.000000),
        ('A123456789', N'玉山新世紀基金－A累積型',            25.91, 0.021250),
        ('A123456789', N'群益店頭市場基金',                  364.00, 0.028750),
        ('A123456789', N'復華全球平衡基金（台幣）',            43.82, 0.014250),
        ('A123456789', N'群益亞太新趨勢平衡基金',              31.47, 0.019875),
        ('A123456789', N'安聯中華新思路基金（台幣）',          30.36, 0.023625),
        ('A123456789', N'玉山多元收益組合基金－累積型',        13.76, 0.001425),
        ('A123456789', N'聯博多元資產收益組合基金N配息（台幣）', 8.88, 0.000000);
    -- 3. 喜好清單資料（5 筆）
    INSERT INTO FAVORITE.LIKE_LIST (
        PRODUCT_NO,
        PURCHASE_QUANTITY,
        ACCOUNT,
        TOTAL_FEE,
        TOTAL_AMOUNT
    )
    SELECT
        P.NO,
        3,
        '012345678901',
        ROUND(P.PRICE * 3 * P.FEE_RATE, 2),
        ROUND(
            P.PRICE * 3 +
            ROUND(P.PRICE * 3 * P.FEE_RATE, 2),
            2
        )
    FROM FINANCIAL.PRODUCT P
    WHERE P.USER_ID = 'A123456789'
      AND P.PRODUCT_NAME IN (
          N'玉山中小型股基金－A類型',
          N'玉山高成長基金－TISA類型',
          N'玉山貨幣市場基金',
          N'玉山新世紀基金－A累積型',
          N'群益店頭市場基金'
      );
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
