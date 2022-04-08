package cn.qingweico.files;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Configuration
public class GridFsConfig {

   @Value("${spring.data.mongodb.database}")
   private String mongodb;


   @Bean
   public GridFSBucket getGridFsBucket(MongoClient mongoClient) {
      MongoDatabase mongodbDatabase = mongoClient.getDatabase(mongodb);
      return GridFSBuckets.create(mongodbDatabase);
   }
}
