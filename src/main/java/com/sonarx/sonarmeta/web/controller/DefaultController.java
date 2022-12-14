package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.common.EthTransactionException;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.service.Web3Service;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Description: Default controller for test.
 * @author: liuxuanming
 */
@Api("测试接口")
@RestController
@RequestMapping
public class DefaultController {

    @Resource
    Web3Service web3Service;

    @RequestMapping(value = "/error", method = {RequestMethod.POST, RequestMethod.GET})
    public HttpResult error() {
        return HttpResult.errorResult("服务器内部错误");
    }

    @RequestMapping(value = "/index", method = {RequestMethod.GET})
    public String index() {
        return "Hello Index Page!";
    }

    @RequestMapping(value = "/test", method = {RequestMethod.GET})
    public HttpResult test() throws EthTransactionException {
        web3Service.transferERC20UsingSonarMetaAllowance(
                "0x9715ee8a90e33d9b28a2935fc7270d73aaf7f83f",
                "0x58f1098e8d981b8cb9c02e66b39775f38ddbfe22",
                0.66);
        return HttpResult.successResult();
    }

    // Test PathVariable
    @RequestMapping(value = "/index/pathvariable/{params}", method = {RequestMethod.GET})
    public Map<String, Object> pathVariableTest(@PathVariable("params") String param) {
        return new HashMap<String, Object>() {{
            put("PathVariable param", param);
        }};
    }

    // Test RequestParam
    @RequestMapping(value = "/index/requestparam", method = {RequestMethod.GET})
    public Map<String, Object> requestParamTest(@RequestParam("params") String param) {
        return new HashMap<String, Object>() {{
            put("RequestParam param", param);
        }};
    }

    // Test RequestBody
    @RequestMapping(value = "/index/requestbody", method = {RequestMethod.POST})
    public String requestBodyTest(@RequestBody String param) {
        return "body is: " + param;
    }

    // Test text
    // POST Request, HttpServletRequest
    @RequestMapping(value = "/index/text", method = {RequestMethod.POST})
    public String textTest(HttpServletRequest request) {
        ServletInputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            System.out.println(sb.toString());
            return "获取到的文本内容为：" + sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

