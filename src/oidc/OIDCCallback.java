package oidc;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Servlet implementation class OIDCCallback
 */
@WebServlet("/callback")
public class OIDCCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public OIDCCallback() {
		super();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession sess = req.getSession();
		if(sess == null) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN,"No Session");
			return;
		}

		String state = String.valueOf(sess.getAttribute("state"));
		String nonce = String.valueOf(sess.getAttribute("nonce"));
		if( ! state.equals(req.getParameter("state"))) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid state");
			return;
		}

		String code = req.getParameter("code");
		if(code == null || code.isEmpty()) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid code");
			return;
		}

		AuthorizationCodeTokenRequest authreq = new AuthorizationCodeTokenRequest(
				new NetHttpTransport()
				, new JacksonFactory()
				, new GenericUrl(OIDCConsts.TOKEN_URL)
				, code
		);
		authreq.setRedirectUri(OIDCConsts.REDIRECT_SERVER + req.getContextPath() + OIDCConsts.REDIRECT_URI)
				.setClientAuthentication(
			                  new BasicAuthentication(OIDCConsts.CLIENT_ID, OIDCConsts.CLIENT_SECRET)
			    );

		HttpResponse httpres =  authreq.executeUnparsed();
		IdTokenResponse idtokenres = httpres.parseAs(IdTokenResponse.class);

		String accessToken = idtokenres.getAccessToken();

		IdToken idToken = IdToken.parse(idtokenres.getFactory(), idtokenres.getIdToken());
		try {
			if( ! idToken.verifySignature(OIDCUtil.getYConnectPublicKey(idToken.getHeader().getKeyId()))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid signature");
				return;
			}
			if( ! idToken.verifyIssuer(Arrays.asList(OIDCConsts.ISSUER_URL))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid issuer");
				return;
			}
			if( ! idToken.verifyAudience(Arrays.asList(OIDCConsts.CLIENT_ID))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid audience");
				return;
			}
			if( ! nonce.equals(idToken.getPayload().getNonce())) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid nonce");
				return;
			}
			req.getServletContext().log(accessToken);
			req.getServletContext().log(OIDCUtil.getAtHash(accessToken));
			req.getServletContext().log(idToken.getPayload().getAccessTokenHash());
			if( ! OIDCUtil.getAtHash(accessToken).equals(idToken.getPayload().getAccessTokenHash())) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid at_hash");
				return;
			}
			if( ! idToken.verifyExpirationTime(System.currentTimeMillis(),0)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid Expiration Time");
				return;
			}
			if( ! idToken.verifyIssuedAtTime(System.currentTimeMillis(), 600)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid Issued At");
				return;
			}

			String userInfoJsonStr = OIDCUtil.getUserInfo(accessToken);
			res.setContentType("text/plain");
			res.setCharacterEncoding("UTF-8");
			PrintWriter pw = new PrintWriter(res.getOutputStream());
			pw.println(userInfoJsonStr);
			pw.flush();
			pw.close();
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
}
