package com.powroznik.jsplists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.auth.AuthPolicy;

public class Demo {
	public static void main(String[] args) throws Exception {
		ListsStub stub = new ListsStub();

		HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
		auth.setUsername("");
		auth.setPassword("");
		auth.setDomain("");
		auth.setHost("");

		List<String> authPrefs = new ArrayList<String>();
		authPrefs.add(AuthPolicy.NTLM);
		auth.setAuthSchemes(authPrefs);

		stub._getServiceClient().getOptions()
				.setProperty(HTTPConstants.AUTHENTICATE, auth);

		JSpLists achievementList = new JSpLists(stub, "Achievements");
		JSpLists teamList = new JSpLists(stub, "Teams");

		{
			{
				Map<String, Object> achievement = new HashMap<String, Object>();
				achievement.put("Title", "Bronze Medal");
				achievementList.newItem(achievement);
			}
			{
				Map<String, Object> achievement = new HashMap<String, Object>();
				achievement.put("Title", "Silver Medal");
				achievementList.newItem(achievement);
			}
			{
				Map<String, Object> achievement = new HashMap<String, Object>();
				achievement.put("Title", "Gold Medal");
				achievementList.newItem(achievement);
			}
		}

		{
			{
				Map<String, Object> team = new HashMap<String, Object>();
				team.put("Title", "Team Red");
				teamList.newItem(team);
			}
			{
				Map<String, Object> team = new HashMap<String, Object>();
				team.put("Title", "Team Green");
				teamList.newItem(team);
			}
			{
				Map<String, Object> team = new HashMap<String, Object>();
				team.put("Title", "Team Blue");
				teamList.newItem(team);
			}
		}
	}
}
