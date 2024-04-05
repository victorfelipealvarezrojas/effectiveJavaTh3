package CreationalPatternsCourse.SimpleFactory.coffeepoweredcrew;

/**
 * This class acts as a simple factory for creation of
 * different posts on website.
 *
 */
public class GenericPostFactory {
    public static <T extends Post> T createPost(Class<T> clazz) {
        return switch (clazz.getSimpleName()) {
            case "BlogPost" -> (T) new BlogPost();
            case "NewsPost" -> (T) new NewsPost();
            case "ProductPost" -> (T) new ProductPost();
            default -> throw new IllegalArgumentException("Post type is unknown");
        };
    }
}
