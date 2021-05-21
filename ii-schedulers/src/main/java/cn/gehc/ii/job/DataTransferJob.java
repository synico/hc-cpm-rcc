package cn.gehc.ii.job;

import cn.gehc.cpm.jobs.TimerDBReadJob;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataTransferJob extends TimerDBReadJob {

    @Value("${custom.url}")
    private String url2Post;

    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse;
        ObjectMapper mapper = new ObjectMapper();
        String result2Post;
        int statusCode = 0;
        try {
            result2Post = mapper.writeValueAsString(body);
            log.info("json: {}", result2Post);

            HttpPost httpPost = new HttpPost(url2Post);
            StringEntity entity = new StringEntity(result2Post, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("send data failed, {}", httpResponse.toString());
            }
            if (statusCode == HttpStatus.SC_OK) {
                log.info("send data success");
            }
        } catch (JsonProcessingException exception) {
            log.error(exception.toString());
        } catch (ClientProtocolException exception) {
            log.error(exception.toString());
        } catch (IOException exception) {
            log.error(exception.toString());
        }

        Map lastEntry = body.stream().reduce((first, second) -> second).orElse(null);
        if (lastEntry != null) {
            Object dtLastUpdate = lastEntry.get("joint_key");
            log.info("lastPolledValue: {}", dtLastUpdate);
            if (dtLastUpdate != null && statusCode == HttpStatus.SC_OK) {
                super.updateLastPullValue(headers, dtLastUpdate.toString());
            }
        }
    }
}
