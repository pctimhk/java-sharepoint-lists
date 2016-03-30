Super simple (and super basic) SharePoint 2013 webservice interface written in Java.

```
JSpLists teamList = new JSpLists(stub, "Teams");
Map<String, Object> team = new HashMap<String, Object>();
team.put("Title", "Team Green");
teamList.newItem(team);
```