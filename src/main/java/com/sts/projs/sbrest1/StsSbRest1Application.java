package com.sts.projs.sbrest1;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class StsSbRest1Application {

	public static void main(String[] args) {
		SpringApplication.run(StsSbRest1Application.class, args);
	}

}

@ControllerAdvice
class StsSbRest1GlobalErrorHandler{
	private static final Logger logger = LoggerFactory.getLogger(StsSbRest1GlobalErrorHandler.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGlobalExceptions(Exception ex) {
		logger.error("!!!!!!!! Processing error in handleGlobalExceptions {} !!!!!!!!!", ex.getCause());
		
		return "Got Error while processing request " + ex;
		
	}
	
}

class Blog {

	private static final Logger logger = LoggerFactory.getLogger(Blog.class);

	private int blogId;
	private String title;
	private LocalDateTime createDt;
	private String author;
	private String content;

	public Blog() {
		logger.info("Blog created by {}", Thread.currentThread().getName());
	}

	public Blog(String title, LocalDateTime createDt, String author, String content) {
		super();

		this.title = title;
		this.createDt = createDt;
		this.author = author;
		this.content = content;
	}

	public int getBlogId() {
		return blogId;
	}

	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreateDt() {
		return createDt;
	}

	public void setCreateDt(LocalDateTime createDt) {
		this.createDt = createDt;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

interface BlogService {
	List<Blog> getAllBlogs();
	void addBlog(Blog newBlog) throws Exception;
}

@Service
class MockBlogService implements BlogService {

	private static final Logger logger = LoggerFactory.getLogger(MockBlogService.class);

	private static List<Blog> inMemBlogDb = null;
	
	static {
		logger.info("@@@@@@@@@@ Begin loading blogs data@@@@@@@@");
		initMockBlogDb();
		logger.info("@@@@@@@@@@ Loaded in memmory blog data {} @@@@@@@@@@ ", inMemBlogDb);
	}

	private static void initMockBlogDb() {
		inMemBlogDb = Arrays
				.asList("Blog-title1", "Blog-title2", "Blog-title3").stream().map(title -> new Blog(title,
						LocalDateTime.now().minusDays(title.length()), "author-" + title, "content-" + title))
				.collect(Collectors.toList());

		// set the ids
		for (int i = 0; i < inMemBlogDb.size(); i++) {
			logger.info("Setting the blogs ids {}", i);
			inMemBlogDb.get(i).setBlogId(i + 1);
		}
	}

	@Override
	public List<Blog> getAllBlogs() {
		logger.info("Got service request to get all blogs ");
		return inMemBlogDb;
	}

	
	public void addBlog(Blog newBlog) throws Exception {
		logger.info("Service adding new blog {}", newBlog);
		
		Blog blogInDb = inMemBlogDb.stream().filter(blog -> blog.getBlogId() == newBlog.getBlogId()).findAny().orElse(null);
		
		if(blogInDb !=null) {
			logger.error("Blog with id {} already present", newBlog.getBlogId());
			throw new Exception("Blog with id " + newBlog.getBlogId() + " already present!");
		}else {
			logger.info("inserting new blog{} ", newBlog);
			inMemBlogDb.add(newBlog);
		}
		
	}
}

@RestController
@RequestMapping(path = "/api/v1")
class BlogRestController {
	private static final Logger logger = LoggerFactory.getLogger(BlogRestController.class);
	private static final AtomicInteger blogReqCount = new AtomicInteger(1);

	@Autowired
	private BlogService blogService;

	@GetMapping(path = "/blogs")
	public List<Blog> getAllBlogs() {
		logger.info("Got request {} for all blogs", blogReqCount.getAndIncrement());
		// get mock blog data
		return blogService.getAllBlogs();
	}
	
	@PostMapping(path = "/blogs")
	public ResponseEntity<Object> createBlog(@RequestBody Blog newBlog) throws Exception {
		logger.info("Got create request for new blog {}", newBlog);
		blogService.addBlog(newBlog);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}