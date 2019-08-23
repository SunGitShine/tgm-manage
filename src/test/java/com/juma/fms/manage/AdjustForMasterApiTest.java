package com.juma.fms.manage;

import com.alibaba.fastjson.JSON;
import com.bruce.tool.rpc.http.core.Https;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.juma.fms.manage.base.Result;
import com.juma.tgm.fms.domain.v3.enums.AdjustMasterType;
import com.juma.tgm.fms.domain.v3.enums.AdjustType;
import com.juma.tgm.fms.domain.v3.vo.WaybillCustomerExportVO;
import com.juma.tgm.fms.domain.v3.vo.WaybillVendorExportVO;
import me.about.poi.reader.XlsxReader;
import me.about.poi.writer.XlsxWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 运力输送,接口测试类
 * 功能 :
 * 1.仅仅测试参数是否能正常获取.
 * 2.如果能连本地dubbo,可以尝试,manage访问本地service(目前未实现).
 * @author : Bruce(刘正航) 17:48 2019-03-29
 */
public class AdjustForMasterApiTest {

    private static final String baseUrl = "http://127.0.0.1:8080";
//    private static final String baseUrl = "http://10.101.0.105:8088";
    private static final Map<String,Object> headers = Maps.newHashMap();
    static{
        // 此处的信息, 通过浏览器登录之后, 从浏览器Cookie中获取
        headers.put("Cookie","GSESSIONID=18F992ADF2744E2695C9DB281EAE360D; userId=1; JSESSIONID=60D4748C19DA4CA1DFC0ABCD0037EB83");
//        headers.put("Content-Type","application/json");
    }

    @Test
    public void do_upload_adjust_approval_excel() throws IOException {
        String result = Https.create().url(baseUrl+"/adjust/upload/waybill.html")
                .print(true)
                .addAllHeaders(headers)
                .addBody("attachs",new FileInputStream(System.getProperty("user.home")+"/Desktop/test5.xls"))
                .addBody("adjustForWho", AdjustMasterType.CUSTOMER.getCode())
                .addBody("adjustType", AdjustType.BEFORE.getCode())
                .post();
        Result listResult1 = new Gson().fromJson(result, Result.class);
        assertNotNull(listResult1);
        assertTrue(listResult1.getCode() == 0);
    }

    @Test
    public void uploadTest() throws Exception {
        FileInputStream bais = new FileInputStream(System.getProperty("user.home")+"/Downloads/运单原始数据.xlsx");
        List<WaybillCustomerExportVO> list = XlsxReader.fromInputStream(bais,WaybillCustomerExportVO.class);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void decode() throws UnsupportedEncodingException {
        System.out.println(new String("Excel鏂囦欢鏍囬\uE57D鏍忛敊璇\uE224紝璇锋\uE5C5鏌ヤ笅".getBytes("gbk")));
    }

    @Test
    public void testOutAndImport() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XlsxWriter.toOutputStream(Lists.newArrayList(new WaybillVendorExportVO()),baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        XlsxReader.fromInputStream(bais, WaybillVendorExportVO.class);
    }

}
