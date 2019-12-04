package com.okcoin.commons.okex.open.api.test.swap;

import com.alibaba.fastjson.JSONObject;
import com.okcoin.commons.okex.open.api.bean.swap.param.*;
import com.okcoin.commons.okex.open.api.bean.swap.result.ApiCancelOrderVO;
import com.okcoin.commons.okex.open.api.bean.swap.result.ApiOrderResultVO;
import com.okcoin.commons.okex.open.api.bean.swap.result.ApiOrderVO;
import com.okcoin.commons.okex.open.api.bean.swap.result.OrderCancelResult;
import com.okcoin.commons.okex.open.api.service.swap.SwapTradeAPIService;
import com.okcoin.commons.okex.open.api.service.swap.impl.SwapTradeAPIServiceImpl;
import com.okcoin.commons.okex.open.api.test.spot.SpotOrderAPITest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SwapTradeTest extends SwapBaseTest {
    private SwapTradeAPIService tradeAPIService;
    private static final Logger LOG = LoggerFactory.getLogger(SwapTradeTest.class);


    @Before
    public void before() {
        config = config();
        tradeAPIService = new SwapTradeAPIServiceImpl(config);
    }

    /**
     * 下单
     * API交易提供限价单下单模式，只有当您的账户有足够的资金才能下单。
     * 一旦下单，您的账户资金将在订单生命周期内被冻结，被冻结的资金以及数量取决于订单指定的类型和参数。目前api下单只支持以美元为计价单位
     * POST /api/swap/v3/order
     * 限速规则：40次/2s
     */
    @Test
    public void order() {
        PpOrder ppOrder = new PpOrder("ctt1128swap001", "1", "1", "0", "6500", "btc-usd-swap","0");
        final  Object apiOrderVO = tradeAPIService.order(ppOrder);
        this.toResultString(SwapTradeTest.LOG, "orders", apiOrderVO);
        System.out.println("jsonObject:::::"+apiOrderVO);

    }

    /**
     * 批量下单
     * 批量进行下单请求，每个合约可批量下10个单。
     * POST /api/swap/v3/orders
     * 限速规则：20次/2s
     */
    @Test
    public void batchOrder() {

        List<PpBatchOrder> list = new LinkedList<>();
        //list.add(new PpBatchOrder("ctt1127swap02", "1", "1", "0", "6500","0"));
        list.add(new PpBatchOrder("ctt1128swap001", "1", "1", "0", "6500","0"));
        list.add(new PpBatchOrder("ctt1128swap002", "1", "1", "0", "6600","0"));
        PpOrders ppOrders = new PpOrders();
        ppOrders.setInstrument_id("BTC-USD-SWAP");
        ppOrders.setOrder_data(list);
        String jsonObject = tradeAPIService.orders(ppOrders);
        //ApiOrderResultVO apiOrderResultVO = JSONObject.parseObject(jsonObject, ApiOrderResultVO.class);
        System.out.println("success");
        System.out.println(jsonObject);


    }

    /**
     * 撤单
     * 撤销之前下的未完成订单。
     * POST /api/swap/v3/cancel_order/<instrument_id>/<order_id> or <client_oid>
     * 限速规则：40次/2s
     */
    @Test
    public void cancelOrderByOrderId() {
        String jsonObject = tradeAPIService.cancelOrderByOrderId("BTC-USD-SWAP", "375845363402350593");
        ApiCancelOrderVO apiCancelOrderVO = JSONObject.parseObject(jsonObject, ApiCancelOrderVO.class);
        System.out.println("success");
        System.out.println(apiCancelOrderVO.getOrder_id());
    }

    @Test
    public void cancelOrderByClientOid() {
        String jsonObject = tradeAPIService.cancelOrderByClientOid("btc-usd-SWAP", "ctt1128swap001");
        ApiCancelOrderVO apiCancelOrderVO = JSONObject.parseObject(jsonObject, ApiCancelOrderVO.class);
        System.out.println("success");
        System.out.println(apiCancelOrderVO.getOrder_id());
    }

    /**
     * 批量撤单
     * 撤销之前下的未完成订单，每个币对可批量撤10个单。
     * POST /api/swap/v3/cancel_batch_orders/<instrument_id>
     * 限速规则：20次/2s
     */
    @Test
    public void batchCancelOrderByOrderId() {
        //生成一个PpCancelOrderVO对象
        PpCancelOrderVO ppCancelOrderVO = new PpCancelOrderVO();

        ppCancelOrderVO.getIds().add("375867544074919936");
        ppCancelOrderVO.getIds().add("375867544083308544");
        System.out.println(JSONObject.toJSONString(ppCancelOrderVO));
        String jsonObject = tradeAPIService.cancelOrders(instrument_id, ppCancelOrderVO);
        OrderCancelResult orderCancelResult = JSONObject.parseObject(jsonObject, OrderCancelResult.class);
        System.out.println("success");
        System.out.println(orderCancelResult.getInstrument_id());
    }
    @Test
    public void batchCancelOrderByClientOid() {
        PpCancelOrderVO ppCancelOrderVO = new PpCancelOrderVO();
        List<String> oidlist = new ArrayList<String>();

        oidlist.add("ctt1128swap001");
        oidlist.add("ctt1128swap002");
        ppCancelOrderVO.setClientOids(oidlist);

        System.out.println(JSONObject.toJSONString(ppCancelOrderVO));
        String jsonObject = tradeAPIService.cancelOrders("BTC-USD-SWAP", ppCancelOrderVO);
        OrderCancelResult orderCancelResult = JSONObject.parseObject(jsonObject, OrderCancelResult.class);
        System.out.println("success");
        System.out.println(orderCancelResult.getInstrument_id());
    }

    /**
     * 委托策略下单
     * 提供止盈止损、跟踪委托、冰山委托和时间加权委托策略
     * POST /api/swap/v3/order_algo
     * 限速规则：40次/2s
     */
    @Test
    public void testSwapOrderAlgo(){
        SwapOrderParam swapOrderParam=new SwapOrderParam();
        swapOrderParam.setInstrument_id("BTC-USD-SWAP");
        swapOrderParam.setType("1");
        swapOrderParam.setOrder_type("1");
        swapOrderParam.setSize("1");
        swapOrderParam.setTrigger_price("7000");
        swapOrderParam.setAlgo_price("6800");

        String jsonObject = tradeAPIService.swapOrderAlgo(swapOrderParam);
        System.out.println("---------success--------");
        System.out.println(jsonObject);
    }

    /**
     * 委托策略撤单
     * 根据指定的algo_id撤销某个合约的未完成订单，每次最多可撤6（冰山/时间）/10（计划/跟踪）个。
     * POST /api/swap/v3/cancel_algos
     * 限速规则：20 次/2s
     */
    @Test
    public void testCancelOrderAlgo(){
        CancelOrderAlgo cancelOrderAlgo=new CancelOrderAlgo();
        cancelOrderAlgo.setAlgo_ids("375877452747186176");
        cancelOrderAlgo.setInstrument_id("BTC-USD-SWAP");
        cancelOrderAlgo.setOrder_type("1");

        String jsonObject = tradeAPIService.cancelOrderAlgo(cancelOrderAlgo);
        System.out.println("---------success--------");
        System.out.println(jsonObject);
    }

    /**
     * 获取委托单列表
     * 列出您当前所有的订单信息。
     * GET /api/swap/v3/order_algo/<instrument_id>
     * 限速规则：20次/2s
     */
    @Test
    public void testGetSwapAlgOrders(){
        System.out.println("begin to show the swapAlgpOrders");
        String jsonObject = tradeAPIService.getSwapOrders("BTC-USD-SWAP",
                                                            "1",
                                                            "",
                                                            "375877452747186176",
                                                            "",
                                                            "",
                                                            "20");
        System.out.println(jsonObject);
    }


}
