package com.powroznik.jsplists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;

public class JSpLists {
	private Map<String, JSpListsConverter> converters = new HashMap<String, JSpListsConverter>();

	private Map<String, String> fieldNames = new HashMap<String, String>();

	private Map<String, String> fieldTypes = new HashMap<String, String>();

	private String listName;

	private ListsStub stub;

	public JSpLists(ListsStub stub, String listName) {
		try {
			this.stub = stub;
			this.listName = listName;

			ListsStub.GetList req = new ListsStub.GetList();
			req.setListName(listName);

			ListsStub.GetListResponse res = stub.getList(req);
			OMElement listElem = res.getGetListResult().getExtraElement();
			JSpListsUtils.log("listElem = " + listElem);

			Iterator<OMElement> fieldsIter = listElem
					.getChildrenWithLocalName("Fields");
			while (fieldsIter.hasNext()) {
				OMElement fieldsElem = fieldsIter.next();
				Iterator<OMElement> fieldIter = fieldsElem
						.getChildrenWithLocalName("Field");
				while (fieldIter.hasNext()) {
					OMElement fieldElem = fieldIter.next();

					if (!JSpListsUtils.isBlank(JSpListsUtils.getAttributeValue(
							fieldElem, "FieldRef"))) {
						continue;
					}
					if (!JSpListsUtils.contains(
							JSpListsConstants.DEFAULT_SUPPORTED_TYPES,
							JSpListsUtils.getAttributeValue(fieldElem, "Type"))) {
						continue;
					}

					fieldNames.put(JSpListsUtils.getAttributeValue(fieldElem,
							"DisplayName"), JSpListsUtils.getAttributeValue(
							fieldElem, "Name"));
					fieldTypes.put(JSpListsUtils.getAttributeValue(fieldElem,
							"DisplayName"), JSpListsUtils.getAttributeValue(
							fieldElem, "Type"));

					converters.put(JSpListsUtils.getAttributeValue(fieldElem,
							"DisplayName"), new JSpListsDefaultConverter(
							fieldTypes, fieldElem));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteItem(Map<String, Object> item) {
		newOrUpdateOrDeleteItem("Delete", item);
	}

	private String getFieldName(String displayName) {
		return fieldNames.get(displayName);
	}

	public Map<String, Object> getListItemById(Long id) {
		List<JSpListsFilter> filters = new ArrayList<JSpListsFilter>();
		filters.add(new JSpListsFilter("ID", JSpListsConstants.COMPARISON_EQ,
				id));
		List<Map<String, Object>> listItems = getListItems(filters);
		if (!listItems.isEmpty()) {
			return listItems.get(0);
		}
		return null;
	}

	public List<Map<String, Object>> getListItems(List<JSpListsFilter> filters) {
		try {
			ListsStub.GetListItems req = new ListsStub.GetListItems();
			req.setListName(listName);

			int filterSize = filters != null ? filters.size() : 0;
			if (filterSize != 0) {
				OMElement queryElem = JSpListsUtils.createElement("Query");
				OMElement whereElem = JSpListsUtils.createElement(queryElem,
						"Where");
				OMElement andElem = filterSize == 1 ? null : JSpListsUtils
						.createElement(whereElem, "And");
				for (JSpListsFilter filter : filters) {
					OMElement eqElem = JSpListsUtils.createElement(
							filterSize == 1 ? whereElem : andElem,
							JSpListsUtils.getComparisonElemName(filter
									.getComparison()));
					OMElement fieldRefElem = JSpListsUtils.createElement(
							eqElem, "FieldRef");
					JSpListsUtils.addAttribute(fieldRefElem, "Name",
							fieldNames.get(filter.getDisplayName()));
					OMElement valueElem = JSpListsUtils.createElement(eqElem,
							"Value");
					JSpListsUtils.addAttribute(valueElem, "Type",
							fieldTypes.get(filter.getDisplayName()));
					if (JSpListsUtils
							.equalsIgnoreCase(
									fieldTypes.get(filter.getDisplayName()),
									"DATETIME")) {
						JSpListsUtils.addAttribute(valueElem,
								"IncludeTimeValue", "TRUE");
					}
					valueElem.setText(converters.get(filter.getDisplayName())
							.object2String(filter.getValue()));
				}
				JSpListsUtils.log("queryElem = " + queryElem);
				req.setQuery(new ListsStub.Query_type0());
				req.getQuery().setExtraElement(queryElem);
			}

			req.setRowLimit("0");

			ListsStub.GetListItemsResponse res = stub.getListItems(req);
			OMElement listItemsElem = res.getGetListItemsResult()
					.getExtraElement();
			JSpListsUtils.log("listItemsElem = " + listItemsElem);

			OMElement dataElem = JSpListsUtils.getFirstElement(listItemsElem);
			Iterator<OMElement> rowIter = dataElem
					.getChildrenWithLocalName("row");
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			while (rowIter.hasNext()) {
				OMElement rowElem = rowIter.next();
				Map<String, Object> item = new HashMap<String, Object>();
				for (String key : fieldNames.keySet()) {
					item.put(
							key,
							converters.get(key).string2object(
									JSpListsUtils.getAttributeValue(rowElem,
											"ows_" + fieldNames.get(key))));
				}
				listItems.add(item);
			}
			return listItems;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void newItem(Map<String, Object> item) {
		newOrUpdateOrDeleteItem("New", item);
	}

	private void newOrUpdateOrDeleteItem(String cmd, Map<String, Object> item) {
		try {
			ListsStub.UpdateListItems req = new ListsStub.UpdateListItems();
			req.setListName(listName);

			OMElement batchElem = JSpListsUtils.createElement("Batch");
			OMElement methodElem = JSpListsUtils.createElement(batchElem,
					"Method");
			JSpListsUtils.addAttribute(methodElem, "ID", "1");
			JSpListsUtils.addAttribute(methodElem, "Cmd", cmd);
			for (String key : item.keySet()) {
				OMElement fieldElem = JSpListsUtils.createElement(methodElem,
						"Field");
				JSpListsUtils
						.addAttribute(fieldElem, "Name", getFieldName(key));
				fieldElem.setText(converters.get(key).object2String(
						item.get(key)));
			}
			req.setUpdates(new ListsStub.Updates_type1());
			req.getUpdates().setExtraElement(batchElem);
			JSpListsUtils.log("batchElem = " + batchElem);

			ListsStub.UpdateListItemsResponse res = stub.updateListItems(req);
			OMElement resultsElem = res.getUpdateListItemsResult()
					.getExtraElement();
			JSpListsUtils.log("resultsElem = " + resultsElem);

			OMElement errorCodeElem = JSpListsUtils.getFirstElement(
					JSpListsUtils.getFirstElement(resultsElem), "ErrorCode");
			JSpListsUtils.log("errorCodeElem = " + errorCodeElem);
			OMElement errorTextElem = JSpListsUtils.getFirstElement(
					JSpListsUtils.getFirstElement(resultsElem), "ErrorText");
			if (!JSpListsUtils.equalsIgnoreCase(
					JSpListsUtils.getText(errorCodeElem), "0x00000000")) {
				throw new Exception(JSpListsUtils.getText(errorTextElem));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setConverter(String displayName, JSpListsConverter converter) {
		converters.put(displayName, converter);
	}

	public void updateItem(Map<String, Object> item) {
		newOrUpdateOrDeleteItem("Update", item);
	}
}
