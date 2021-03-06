package controllers;

import models.PostComment;
import play.libs.Json;
import models.BlogPost;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.*;

import static controllers.Application.buildJsonResponse;

/**
 * Created by antoniel on 30/10/15.
 */
public class Post extends Controller {

    public Result addPost() {
        Form<PostForm> postForm = Form.form(PostForm.class).bindFromRequest();

        if (postForm.hasErrors()) {
            return badRequest(postForm.errorsAsJson());
        } else {
            BlogPost newBlogPost = new BlogPost();
            newBlogPost.commentCount = 0L;
            newBlogPost.subject = postForm.get().subject;
            newBlogPost.content = postForm.get().content;
            newBlogPost.user = getUser();
            newBlogPost.save();
        }
        return ok(buildJsonResponse("success", "Post added successfully"));
    }

    public Result getPosts() {
        return ok(Json.toJson(BlogPost.find.findList()));
    }

    public Result getUserPosts() {
        User user = getUser();
        if(user == null) {
            return badRequest(Application.buildJsonResponse("error", "No such user"));
        }
        return ok(Json.toJson(BlogPost.findBlogPostsByUser(user)));
    }

    private User getUser() {
        return User.findByEmail(session().get("username"));
    }

    public static class PostForm {

        @Constraints.Required
        @Constraints.MaxLength(255)
        public String subject;

        @Constraints.Required
        public String content;

    }

    public Result getPost(Long id){
        BlogPost blogPost = BlogPost.findBlogPostById(id);
        if (blogPost == null){
            return notFound(buildJsonResponse("erro","Post not found"));
        }
        return ok(Json.toJson(blogPost));
    }

    public Result addComment() {
        Form<CommentForm> commentForm = Form.form(CommentForm.class).bindFromRequest();

        if (commentForm.hasErrors()) {
            return badRequest(commentForm.errorsAsJson());
        } else {
            PostComment newComment = new PostComment();
            BlogPost blogPost = BlogPost.findBlogPostById(commentForm.get().postId);
            blogPost.commentCount++;
            blogPost.save();
            newComment.blogPost = blogPost;
            newComment.user = getUser();
            newComment.content = commentForm.get().comment;
            newComment.save();
            return ok(Application.buildJsonResponse("success", "Comment added successfully"));
        }
    }


    public static class CommentForm {

        @Constraints.Required
        public Long postId;

        @Constraints.Required
        public String comment;

    }
}
