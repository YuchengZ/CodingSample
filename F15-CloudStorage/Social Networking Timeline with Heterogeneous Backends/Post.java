package cc.cmu.edu.minisite;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * class of a table in dynamoDB
 * @author ZhangYC
 *
 */
@DynamoDBTable(tableName = "test")
public class Post implements Comparable<Post>{
    private int id;
    private String timestamp;
    private String post;

    @DynamoDBHashKey(attributeName = "UserID")
    public int getUserID() {
        return id;
    }

    public void setUserID(int id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute(attributeName = "Post")
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return String.format("%d\t%s\t%s", id, timestamp, post);
    }

	@Override
	public int compareTo(Post o) {
		return -1 * timestamp.compareTo(o.timestamp);
	}

}
