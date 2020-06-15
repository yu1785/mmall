package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yu
 * @date 2020/6/13 21:09
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return null;
    }




    /************** 支付模块 ****************/

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    // 支付宝的参数都是放在 request 里面的
    public Object alipayCallback(HttpServletRequest request){
        /*存放支付宝的参数*/
        Map<String,String> params = Maps.newHashMap();

        Map requestParames = request.getParameterMap();
        for (Iterator iter = requestParames.keySet().iterator();iter.hasNext();){
            String name = (String) iter.next();
            String[] values = (String[]) requestParames.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i==values.length-1) ? valueStr+values[i] : valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        // 非常重要 验证回调的正确性. 是不是支付宝发的,并且还要避免重复通知.
        /*
        异步返回结果验签
            1.在通知返回参数列表中 除去sign,sign_type两个参数外,凡是通知返回回来的参数皆是待验签的参数.
        */
        params.remove("sign_type");
        try {
            boolean alipayRSACheckdV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckdV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求就找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }

        //TODO 验证各种数据

        // 业务逻辑
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    // 前端查询订单状态
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

}
