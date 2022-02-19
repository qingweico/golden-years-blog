package cn.qingweico.files;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
@Configuration
public class  GridFSConfig {

   @Value("${spring.data.mongodb.database}")
   private String mongodb;


   @Bean
   public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
      MongoDatabase mongodbDatabase = mongoClient.getDatabase(mongodb);
      return GridFSBuckets.create(mongodbDatabase);
   }
}
