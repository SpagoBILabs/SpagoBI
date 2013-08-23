/*
*
* @file PaloConfiguration.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: PaloConfiguration.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PaloConfiguration {

    public static final int DEFAULT_POOL_MAX_CONNECTIONS = 10;

    private List servers = new ArrayList();
    private int poolMaxConnections = DEFAULT_POOL_MAX_CONNECTIONS;
    private String user = "guest";
    private String password = "pass";
    
    public final List getServers() {
        return servers;
    }

    public final void setServers(List servers) {
        this.servers = servers;
    }

    public int getPoolMaxConnections() {
        return poolMaxConnections;
    }

    public void setPoolMaxConnections(int value) {
        poolMaxConnections = value;
    }
    
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

    public void addServer(PaloServer server){
        servers.add(server);
        Collections.sort(servers, new ServerOrderComparator());
    }

    public PaloServer getServer(int order){
        PaloConfiguration.PaloServer r = null;
        for (Iterator it = servers.iterator(); it.hasNext();) {
            PaloServer server = (PaloServer) it.next();
            if(server.getOrder() == order){
                r = server;
                break;
            }
        }
        return r;
    }
    
    public PaloServer getServer(String host, String service){
        PaloConfiguration.PaloServer r = null;
        for (Iterator it = servers.iterator(); it.hasNext();) {
            PaloServer server = (PaloServer) it.next();
            if (host.equals(server.getHost()) &&
            	service.equals(server.getService())){
                r = server;
                break;
            }
        }
        return r;
    }
    
    public String toString() {
        String result = "PaloConfiguration[";
        int size = servers.size();
        for( int i = 0 ; i < size ; i++ ) { 
            result += servers.get(i)+"\n";
        } 
        result += "connection.pool.max = "+getPoolMaxConnections();
        result += "]";
        return result;
    }

    public static final class PaloServer{

    	private final static String DEFAULT_PROVIDER = "palo";
        private String host;
        private int order;
        private String service;
        private String login;
        private String password;
        private String provider = DEFAULT_PROVIDER;
		private String dispName;

        public final String getHost() {
            return host;
        }

        public final void setHost(String host) {
            this.host = host;
        }

        public final String getLogin() {
            return login;
        }

        public final void setLogin(String login) {
            this.login = login;
        }

        public final String getPassword() {
            return password;
        }

        public final void setPassword(String password) {
            this.password = password;
        }

        public final String getService() {
            return service;
        }

        public final void setService(String service) {
            this.service = service;
        }

        public String getProvider() {
			return provider;
		}

        public void setProvider(String provider) {
			this.provider = provider;
		}
		
		public String toString() {
			String result = "PaloServer[";
			result += provider + ";";
			result += host+":"+service+";";
			result += login+":" + password;
			result += "]";
			return result;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public void setDispName(String value) {
			dispName = value;
		}
		

		public String getDispName() {
			return dispName;
		}
    }
    
    private static class ServerOrderComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			PaloServer server1 = (PaloServer)o1;
			PaloServer server2 = (PaloServer)o2;
			return server1.getOrder() - server2.getOrder();
		}
    	
    }

}
