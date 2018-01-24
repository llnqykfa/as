package com.aixianshengxian.constant;

/**
 * Created by cwj on 2017/6/3.
 */

public class UrlConstants {
//    public static final String HEAD ="http://erp.lbh.atfresh.cn";
    public static final String HEAD ="http://beta.shipments.atfresh.cn";//测试
//    public static final String HEAD ="http://192.168.3.72:8078";
    /*public static final String HEAD = "http://ifresh.merchant.zxsoon.com";//正式*/
    public static final String URL_LOGIN = HEAD+ "/mdata-controller/mdata/sso/login.do";//登录
    public static final String URL_LOGOUT = HEAD + "/mdata-controller/mdata/sso/logout.do";//注销
    public static final String URL_RED_POINT = HEAD +"/merchant/sale/goodsOrder/queryWaitingQty.do";//获取小红点

    //销售
    public static final String URL_ORDERS = HEAD+"/merchant/pda/sale/query.do";//获取订单
    public static final String URL_GOODS_ORDER = HEAD +"/merchant/pda/sale/get.do";//获取订单详情
    public static final String URL_ORDER_CANCEL = HEAD+"/merchant/sale/goodsOrder/abort.do";//作废订单
    public static final String URL_ORDER_DELETE = HEAD +"/merchant/sale/goodsOrder/delete.do";//删除订单
    public static final String URL_ORDER_AUDIT = HEAD + "/merchant/sale/goodsOrder/audit.do";//审核订单
    public static final String URL_ORDER_EDIT = HEAD+"/merchant/pda/sale/get.do";//编辑订单，获取订单详情
    public static final String URL_SAVE_ORDER_EDIT = HEAD + "/merchant/pda/sale/saveModify.do";//保存编辑订单

    public static final String URL_STORE_LIST = HEAD +"/merchant/wms/warehouse/getDropDownList.do";//获取仓库列表
    public static final String URL_STOCK_OUT =HEAD +"/merchant/sale/goodsOrder/stockOut.do";//出库
    public static final String URL_TRANSPORT =HEAD +"/ctms/transportBill/getsBySourceBillNumber.do";//配送列表
    public static final String URL_CREAT_RETURN_BILL = HEAD +"/merchant/sale/returnBill/create.do";//创建退货单
    public static final String URL_GET_STOCK_IN_RECORD = HEAD + "/merchant/wms/stockOutRecord/getPriceMapByUuids.do";//获取出库成本单价
    public static final String URL_SIGN= HEAD +"/ctms/transportBill/sign.do";//签收
    public static final String URL_REFUND = HEAD+ "/merchant/sale/returnBill/saveNew.do";//退货
    public static final String URL_CLIENTS = HEAD + "/merchant/pda/sale/getDropdownCustomers.do";//获取客户
    public static final String URL_DEPARTMENTS = HEAD +"/merchant/pda/sale/getDropdownDept.do";//获取部门
    public static final String URL_CREATE_NEW_ORDER=HEAD + "/merchant/sale/goodsOrder/create.do";//新建订单
    public static final String URL_SAVE_NEW_ORDER = HEAD + "/merchant/pda/sale/saveNew.do";//保存新建订单
    public static final String URL_QUERY_LINE=HEAD +"/merchant/pda/sale/queryValidLinesForOrder.do";//定价列表
    public static final String URL_TEMP_GOODS_LIST = HEAD +"/merchant/goods/goods/getDropDownGoodsForApp.do";//临时商品列表
    public static final String URL_TEMP_GOODS_UNIT_LIST = HEAD+"/merchant/goods/goods/getDropDownUnitPrice.do";//临时商品单位列表

    //新增内容
    //计划
    public static final String URL_PLAN_GET_LIST = HEAD + "/merchant/pda/plan/getList.do";//计划处获取列表信息
    public static final String URL_PLAN_DELETE = HEAD + "/merchant/pda/plan/delete.do";//计划处删除
    public static final String URL_PLAN_BATCH_SAVE = HEAD + "/merchant/pda/plan/batchSave.do";//计划处批量新增保存
    public static final String URL_PLAN_SAVE_MODIFY = HEAD + "/merchant/pda/plan/saveModify.do";//计划处编辑保存
    public static final String URL_PLAN_GET = HEAD + "/merchant/pda/plan/get.do";//计划处查询
    //采购
    public static final String URL_PURCHASE_SAVE_NEW = HEAD + "/merchant/pda/purchase/saveNew.do";//采购处新增
    public static final String URL_SEARCH_PROVIDER = HEAD + "/merchant/pda/purchase/getDropdownSuppliers.do";//采购处查询供应商
    public static final String URL_PURCHASE_SCAN_DATA = HEAD + "/merchant/pda/purchase/scanToPurchaseData.do";//采购处扫码获取采购信息，验收点击验收
    public static final String URL_PURCHASE_GET= HEAD+"/merchant/pda/purchase/query.do";//查询采购订单
    public static final String URL_PURCHASE_GET_DETAIL= HEAD+"/merchant/pda/purchase/get.do";//查询采购订单
    public static final String URL_PURCHASE_SAVE= HEAD+"/merchant/pda/purchase/saveModify.do";//查询采购订单
    public static final String URL_PURCHASE_AUDIT= HEAD+"/merchant/pda/purchase/audit.do";//根据uuid审核采购订单
    public static final String URL_PURCHASE_ANORT= HEAD+"/merchant/pda/purchase/abort.do";//根据uuid作废采购订单
    public static final String URL_PURCHASE_DELETE = HEAD+"/merchant/pda/purchase/delete.do";//根据uuid删除采购订单
    public static final String URL_PURCHASE_COMPLETE= HEAD+"/merchant/pda/purchase/complete.do";//根据uuid完成采购订单
    public static final String URL_PURCHASE_CANCEL_AUDIT= HEAD+"/merchant/pda/purchase/cancelAudit.do";//根据uuid反审核采购订单
    public static final String URL_PURCHASE_GET_DROPDOWN_OPERATOR= HEAD+"/merchant/pda/purchase/getDropdownOperator.do";//查询经办人列表
    public static final String URL_PURCHASE_GET_PURCHASED_GOODS_BY_SUPPLIER = HEAD + "/merchant/pda/purchase/getPurchasedGoodsBySupplier.do";//根据供应商获取采购过的商品uuid

    //商品
    public static final String URL_DROP_DOWN_GOODS = HEAD + "/merchant/pda/goods/getDropDownGoods.do";//通用商品列表
    public static final String URL_DROP_DOWN_UNIT_PRICE = HEAD + "/merchant/pda/goods/getDropDownUnitPrice.do";//下拉单位查询
    public static final String URL_CATEGORY_TREE = HEAD + "/merchant/pda/goods/getCategoryTree.do";//分类树
    //配送
    public static final String URL_DELIVERY_LIST = HEAD + "/merchant/pda/transport/getList.do";//查询已领配送单列表
    public static final String URL_DELIVERY_SCAN = HEAD + "/merchant/pda/transport/scanToTransport.do";//自动领单
    public static final String URL_DELIVERY_HAND = HEAD + "/merchant/pda/transport/transport.do";//手动领单
    public static final String URL_DELIVERY_DELETE = HEAD + "/merchant/pda/transport/changeToInitial.do";//撤销领单
    //周转筐
    public static final String URL_RECEIVE = HEAD + "/merchant/pda/basket/changeToTransport.do";//领筐
    public static final String URL_RETURNED = HEAD + "/merchant/pda/basket/changeToUnused.do";//还筐
    public static final String URL_UNABLE = HEAD + "/merchant/pda/basket/changeToAborted.do";//报废
    //库存
    public static final String URL_SEARCH_DEPOT = HEAD + "/merchant/pda/wms/getDropDownWarehouse.do";//仓库列表
    public static final String URL_GET_STOCKOUT_RECORD_LIST = HEAD + "/merchant/pda/wms/getStockOutRecordList.do";//获取加工领料
    public static final String URL_STOCK_IN = HEAD + "/merchant/pda/wms/stockIn.do";//入库
    public static final String URL_BATCH_STOCK_IN = HEAD + "/merchant/pda/wms/batchStockIn.do";//批量入库
    public static final String URL_CROSS_DOCKING = HEAD + "/merchant/pda/wms/crossDocking.do";//越库
    public static final String URL_QUERY_STOCK = HEAD + "/merchant/pda/wms/queryStock.do";//查询库存记录
    public static final String URL_STOCK_OUT_2 = HEAD + "/merchant/pda/wms/stockOut.do";//出库
    public static final String URL_INVENTORY = HEAD + "/merchant/pda/wms/inventory.do";//盘点
    public static final String URL_ALLOCATE = HEAD + "/merchant/pda/wms/allocate.do";//调拨
    public static final String URL_SCAN_BY_TRACE_CODE = HEAD + "/merchant/pda/wms/scanByTraceCode.do";//扫描库存标签

    //加工
    public static final String URL_GET_FORECAST_PROCESS_LIST = HEAD + "/merchant/pda/process/getList.do";//根据过滤条件查询加工计划
    public static final String URL_FORECAST_PROCESS_PLAN_SAVE_MODIFY = HEAD + "/merchant/pda/process/saveModify.do";//编辑保存
    public static final String URL_FORECAST_PROCESS_PLAN_SAVE_NEW = HEAD + "/merchant/pda/process/batchSave.do";//新增保存

    //照片上传处
    public static final String URL_UPLOAD_PHOTO = HEAD + "/mdata-controller/mdata/ossUtil/uploadPhoto.do";
}
