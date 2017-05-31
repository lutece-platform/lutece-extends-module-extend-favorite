<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<%@page errorPage="../../../../ErrorPagePortal.jsp" %>
<jsp:useBean id="favoriteJspBean" scope="request" class="fr.paris.lutece.plugins.extend.modules.favorite.web.FavoriteJspBean" />

<%
	try
	{
		favoriteJspBean.doCancelFavorite( request, response );
	}
	catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getSiteMessageUrl( request ) );
	}
%>