package fr.paris.lutece.plugins.extend.modules.favorite.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteListener;
import fr.paris.lutece.portal.service.security.LuteceUser;

public class FavoriteListenerService
{

    private static Map<String, List<IFavoriteListener>> _mapListeners = new HashMap<String, List<IFavoriteListener>>( );
    private static boolean _bHasListeners;

    /**
     * Register a comment listener.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type associated with the listener. Use {@link #CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE} to associated the listener
     *            with every resource type.
     * @param listener
     *            The listener to register
     */
    public static synchronized void registerListener( String strExtendableResourceType, IFavoriteListener listener )
    {
        List<IFavoriteListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners == null )
        {
            listListeners = new ArrayList<IFavoriteListener>( );
            _mapListeners.put( strExtendableResourceType, listListeners );
        }
        listListeners.add( listener );
        _bHasListeners = true;
    }

    /**
     * Check if there is listeners to notify
     * 
     * @return True if there is at last one listener, false otherwise
     */
    public static boolean hasListener( )
    {
        return _bHasListeners;
    }

    /**
     * Notify to listeners new favorite. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param request
     *            the HTTP request
     */
    public static void favorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        List<IFavoriteListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( IFavoriteListener listener : listListeners )
            {
                listener.favorite( strExtendableResourceType, strIdExtendableResource, request );
            }
        }

    }

    /**
     * Notify to listeners canceled favorite. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param request
     *            the HTTP request
     */
    public static void cancelFavorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        List<IFavoriteListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( IFavoriteListener listener : listListeners )
            {
                listener.cancelFavorite( strExtendableResourceType, strIdExtendableResource, request );
            }
        }

    }

    public static boolean canFavorite( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user )
    {
        List<IFavoriteListener> listListeners = _mapListeners.get( strExtendableResourceType );
        boolean res = false;
        if ( listListeners != null )
        {
            for ( IFavoriteListener listener : listListeners )
            {
                return listener.canFavorite( strExtendableResourceType, strIdExtendableResource, user );
            }
        }
        return res;
    }

}
