package annotate;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import annotate.AnnotateHelper.Status;

/**
 * Servlet implementation class AnnotateServlet
 */
public class AnnotateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnnotateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ByteString imgBytes = ByteString.readFrom(request.getInputStream());
		Image img = Image.newBuilder().setContent(imgBytes).build();

		StringBuilder annotateResult = new StringBuilder();
		Status status = AnnotateHelper.annotate(img, annotateResult);
		
		if (status == Status.OK) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.getWriter().print(new JSONObject().put("text", annotateResult.toString()));
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
