package org.javaee7.servlet.simple;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  Super simple test servlet, which also implements ServletContextListener so 
 *  we can test how it responds to SIGTERMs. 
 *  
 *  @author Steve Tran
 */
@WebServlet("/SimpleServlet")
@WebListener
public class SimpleServlet extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = -2866944345034571548L;
	
	// Keep a thread-safe list of running threads
	private static Queue<Thread> threadQueue = new ConcurrentLinkedQueue<Thread>();
	
	/**
	 * Called when the container starts up
	 */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
    }

    /**
     * Called when the container begins to shutdown
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
        
        System.out.println("Waiting for in-flight requests to finish : ");
        int size = threadQueue.size();
        
        System.out.println(size + " remaining");
        
        while(threadQueue.size() > 0) {
        	if(size > threadQueue.size()) {
        		size = threadQueue.size();
        		System.out.println(size + " remaining");
        	}
        }
    }
	
    
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	String sleepTime = request.getParameter("sleep");
    	String retVal = "Here is your GET response, Steve!\n";
       	
    	if(sleepTime != null) {
    		try {
    			System.out.println(String.format("Sleeping for %s seconds", sleepTime));
    			threadQueue.add(Thread.currentThread());
				Thread.sleep(Integer.parseInt(sleepTime)*1000);
				System.out.println("Waking up");
				retVal += "It returned after " + sleepTime + " seconds";
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
		threadQueue.remove(Thread.currentThread());
        response.getWriter().print(retVal);
    }
}