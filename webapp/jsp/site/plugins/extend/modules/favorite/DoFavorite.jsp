<%@page import="fr.paris.lutece.portal.service.security.UserNotSignedException"%>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<%@page import="fr.paris.lutece.portal.web.PortalJspBean"%>
<jsp:useBean id="favoriteJspBean" scope="request" class="fr.paris.lutece.plugins.extend.modules.favorite.web.FavoriteJspBean" />

<%
	try
	{
		favoriteJspBean.doFavorite( request, response );
	}
	catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getSiteMessageUrl( request ) );
	}
	catch( UserNotSignedException unse )
	{
		response.sendRedirect( PortalJspBean.redirectLogin( request ));
	}

%>