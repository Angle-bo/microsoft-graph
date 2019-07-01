package com.luobo.microsoftgraph.utils;

import com.luobo.microsoftgraph.service.OutlookService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Retrofit请求生成器
 */
@Component
public class OutlookServiceBuilder {


    private  final String graph = "https://graph.microsoft.com";

    public  OutlookService getOutlookService(String accessToken, String userEmail) {
        // Create a request interceptor to add headers that belong on
        // every request
        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Builder builder = original.newBuilder()
                        .header("User-Agent", "java-tutorial")
                        .header("client-request-id", UUID.randomUUID().toString())
                        .header("return-client-request-id", "true")
                        .header("Authorization", String.format("Bearer %s", accessToken))
                        .method(original.method(), original.body());

                Request request = builder.build();
                return chain.proceed(request);
            }
        };

        // Create a logging interceptor to log request and responses
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        // Create and configure the Retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(graph)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // Generate the token service
        return retrofit.create(OutlookService.class);
    }
}
