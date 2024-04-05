package CreationalPatternsCourse.SimpleFactory.coffeepoweredcrew;

import java.sql.Array;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) {
        Post post = PostFactory.createPost("blog");
        if (post instanceof BlogPost blogPost) {
            blogPost.setAuthor("victor");
            blogPost.setTags(new String[]{"uno","dos","tres"});
            blogPost.setTitle("Contenido de mi blog");
            blogPost.setContent("body");
        }
        // System.out.println("post = " + post);


        var postGeneric = GenericPostFactory.createPost(BlogPost.class);
        postGeneric.setTags(new String[]{"uno", "dos", "tres","cuatro"});
        postGeneric.setTitle("Contenido de mi blog");

        System.out.println("postGeneric.getTags() = " + Arrays.toString(postGeneric.getTags()));
        
    }
}
