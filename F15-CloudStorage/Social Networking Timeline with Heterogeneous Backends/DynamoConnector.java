package cc.cmu.edu.minisite;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * class connect to DynamoDB
 * @author ZhangYC
 *
 */
public class DynamoConnector {
    private BasicAWSCredentials bawsc;
    private AmazonDynamoDBClient client;
    private DynamoDBMapper mapper;
    private Table table;
    private String tableName = "test";
    
    public DynamoConnector() {
    	
    	// build connection in constructor
        client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());        
        System.out.println("create dynamodb mapper");
        mapper = new DynamoDBMapper(client);
    }
 
    /**
     * get all the post by user id
     * @param id String
     * @return a list of posts
     */
    public List<Post> getPosts(String id) {
    	
    	// get all the post by user id
        Post p = new Post();
        p.setUserID(Integer.parseInt(id));
        DynamoDBQueryExpression<Post> queryExpression = new DynamoDBQueryExpression<Post>()
                .withHashKeyValues(p);

        List<Post> betweenReplies = mapper.query(Post.class, queryExpression);
        return betweenReplies;

        
    }
}
