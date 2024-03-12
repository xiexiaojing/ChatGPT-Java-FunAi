package com.gzhu.funai.service;

import com.gzhu.funai.api.openai.constant.OpenAIConst;
import com.gzhu.funai.api.openai.req.ChatGPTReq;
import com.gzhu.funai.api.openai.resp.ChatGPTResp;
import com.gzhu.funai.enums.ApiType;
import io.milvus.grpc.MutationResult;
import io.milvus.param.R;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author zxw
 * @Desriiption:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFileChatService {

    @Autowired
    private FileChatService fileChatService;
    @Resource
    private AdminApiKeyService adminApiKeyService;
    @Resource
    private PromptService promptService;

    @Test
    public void uploadFile(){
        adminApiKeyService.load();
        promptService.load();
        //生成File文件
        File file = new File("C:\\test\\test.pdf");

        //File文件转MultipartFile
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

            ChatGPTReq chatGPTReq = ChatGPTReq.builder().model(OpenAIConst.MODEL_NAME_CHATGPT_3_5).build();
            String resultR = fileChatService.uploadFile(multipartFile, "", adminApiKeyService.roundRobinGetByType(ApiType.OPENAI), chatGPTReq, false);

            System.out.println("文件处理成功");

            ChatGPTResp resp = fileChatService.chatWithFile("", 85, "文章的创新点是什么？", adminApiKeyService.roundRobinGetByType(ApiType.OPENAI), chatGPTReq, false);

            if(resp != null){
                System.out.println("结果0：" + resp.getMessage());
            }

            ChatGPTResp resp1 = fileChatService.chatWithFile("", 85, "发表这篇文章的机构是谁？", adminApiKeyService.roundRobinGetByType(ApiType.OPENAI), chatGPTReq, false);
            if(resp1 != null){
                System.out.println("结果1：" + resp1.getMessage());
            }

            boolean result = fileChatService.dropCollection("", 85, false);
            if(result){
                System.out.println("关闭连接成功");
            }else{
                System.out.println("关闭连接失败");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Autowired
    DataSourceProperties dataSourceProperties;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testDB(){
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        System.out.println("数据库源："+dataSource);
        System.out.println("类名："+dataSource.getClass().getName());
        System.out.println("HikariDataSource 数据源："+dataSourceProperties);
        //JdbcTemplate 封装一层函数可以直接使用
        Map<String, Object> queryForMap = jdbcTemplate.queryForMap("SELECT * FROM sys_dbserver");
        System.out.println("查询结果Map:"+queryForMap);
    }
}
