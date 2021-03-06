package com.example.springbootdemo.alisms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/sms")
public class SmsDemo {

    // 产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    // 产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台点击个人头像的AccessKey管理即可查看)
    static final String accessKeyId = "LTAI4GFBQ2KtR2GxGgJetYgh";           // TODO 改这里
    static final String accessKeySecret = "0wqk7UZmNnhQFx38h5pjb9UuwKk0ok"; // TODO 改这里


/**
 * 短信发送功能
 * */
    @RequestMapping("/send")
    @ResponseBody
    public static String sendSms(String telephone , HttpServletRequest httpServletRequest) throws ClientException {
        double a =(Math.random()*9+1)*100000;
        String code = String.valueOf(a);
        code = code.split("\\.")[0];
        System.out.println("短信验证码为："+code);
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("code",code);
        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(telephone);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName("ABC商城"); // TODO 改这里
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_196617206");  // TODO 改这里
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");

        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        // hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            System.out.println("短信发送成功！");
        } else {
            System.out.println("短信发送失败！");
        }
        return sendSmsResponse.getMessage();
    }

//    public static void main(String[] args) {
//
//    }

    /**
     * 验证码是否正确
     * */
    @RequestMapping("/iscode")
    @ResponseBody
    public String iscode(String code , HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        String sessioncode = session.getAttribute("code").toString();
        if (sessioncode.equals(code)){
            return "true";
        }else {
            return "false";
        }
    }

}
