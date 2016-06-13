import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class DynamoDBTest<T> {
	private AmazonDynamoDBClient client = new AmazonDynamoDBClient().withEndpoint("http://localhost:8000");
	private DynamoDB dynamoDB = new DynamoDB(client);
	
	public void save(String tableName, T record)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		Class clazz = (Class) record.getClass();
		Table table = dynamoDB.getTable(tableName);
		Item item = new Item();

		Field[] fs = clazz.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			f.setAccessible(true);
			if ("uniqueId".equals(f.getName()))
				item.withPrimaryKey("uniqueId", UUID.randomUUID().toString());
			else {
				String type = f.getType().toString();
				if (type.endsWith("String"))
					item.withString(f.getName(), (String) f.get(record));
				if (type.endsWith("Integer") || type.endsWith("Integer"))
					item.withInt(f.getName(),  (Integer)f.get(record));
				if (type.endsWith("Date")){
					Long time = ((Date)f.get(record)).getTime();
					item.withLong(f.getName(), time);
					if("createTime".equals(f.getName())){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
						String monthStr = sdf.format((Date)f.get(record));
						int month = Integer.parseInt(monthStr);
						item.withInt("month", month);
					}
				}
			}
		}	
		
		table.putItem(item);	
	
	}

	public void batchSave(String tableName, List<T> list)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		TableWriteItems tableWriteItems = new TableWriteItems(tableName);
		for (int j = 0; j < list.size(); j++) {
			Class clazz = (Class) list.get(j).getClass();
			Item item = new Item();
			
			Field[] fs = clazz.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				f.setAccessible(true);
				if ("uniqueId".equals(f.getName()))
					item.withPrimaryKey("uniqueId", UUID.randomUUID().toString());
				else {
					String type = f.getType().toString();
					if (type.endsWith("String"))
						item.withString(f.getName(), (String) f.get(list.get(j)));
					if (type.endsWith("Integer") || type.endsWith("Integer"))
						item.withInt(f.getName(), (Integer) f.get(list.get(j)));
					if (type.endsWith("Date")){
						item.withLong(f.getName(), ((Date)f.get(list.get(j))).getTime());
						if("createTime".equals(f.getName())){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
							String monthStr = sdf.format((Date)f.get(list.get(j)));
							int month = Integer.parseInt(monthStr);
							item.withInt("month", month);
						}
					}
				}
			}
			tableWriteItems.addItemToPut(item);
		}
		
		dynamoDB.batchWriteItem(tableWriteItems);
		
	}

	public List<T> query(String tableName, Date start, Date end, Class<T> clazz)
			throws ParseException, IllegalArgumentException, IllegalAccessException, InstantiationException {
		// TODO Auto-generated method stub
		Table table = dynamoDB.getTable(tableName);
		Index index = table.getIndex("MonthAndCreateTimeIndex");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		int startMonth = Integer.parseInt(sdf.format(start));
		int endMonth = Integer.parseInt(sdf.format(end));
		
		Long startTime = start.getTime();
		Long endTime = end.getTime();
		
		int month = startMonth;		
		List<T> list = new ArrayList<T>();	
		while(month <= endMonth){
			List<T> subList = getByMonth(table,index,month,startTime,endTime,clazz);
			list.addAll(subList);
			month++;
			if(month%100 == 13){
				month += 100;
				month /= 100;
				month *=100;
				month += 1;
			}
		}
		
		return list;
	}

	public int count(String tableName, Date start, Date end, Class<T> clazz)
			throws ParseException, IllegalArgumentException, IllegalAccessException, InstantiationException {
		// TODO Auto-generated method stub
		Table table = dynamoDB.getTable(tableName);
		Index index = table.getIndex("MonthAndCreateTimeIndex");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		int startMonth = Integer.parseInt(sdf.format(start));
		int endMonth = Integer.parseInt(sdf.format(end));
		
		Long startTime = start.getTime();
		Long endTime = end.getTime();
		
		int month = startMonth;			
		int count = 0;
		while(month <= endMonth){
			int n = countByMonth(table,index,month,startTime,endTime,clazz);
			count += n;
			month++;
			if(month%100 == 13){
				month += 100;
				month /= 100;
				month *=100;
				month += 1;
			}
		}
		
		return count;
	}

	public List<T> getByMonth(Table table,Index index,int month,Long startTime, Long endTime,Class<T> clazz) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		NameMap name = new NameMap();
		name.put("#m", "month");
		
		ValueMap value = new ValueMap();
		value.put(":month", month);
		value.put(":start", startTime);
		value.put(":end", endTime);
		
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("#m = :month and createTime between :start and :end")
				.withNameMap(name)
				.withValueMap(value);
		
		ItemCollection<QueryOutcome> items = index.query(spec);
		Iterator<Item> iterator = items.iterator();
		List<T> list = new ArrayList<T>();
		while (iterator.hasNext()){
			Item item = iterator.next();
			T record =toOBject(item,clazz);
			list.add(record);			                                                                                                                                                                                                                                                              ;
		}
		return list;
	}
	
	public int countByMonth(Table table,Index index,int month,Long startTime, Long endTime,Class<T> clazz) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		NameMap name = new NameMap();
		name.put("#m", "month");
		
		ValueMap value = new ValueMap();
		value.put(":month", month);
		value.put(":start", startTime);
		value.put(":end", endTime);
		
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("#m = :month and createTime between :start and :end")
				.withNameMap(name)
				.withValueMap(value);
		
		ItemCollection<QueryOutcome> items = index.query(spec);
		Iterator<Item> iterator = items.iterator();
		List<T> list = new ArrayList<T>();
		while (iterator.hasNext()){
			Item item = iterator.next();
			T record =toOBject(item,clazz);
			list.add(record);			                                                                                                                                                                                                                                                              ;
		}
		return list.size();
	}

	private T toOBject(Item item,Class<T> clazz) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		T record = clazz.newInstance();
		Field[] fs = clazz.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			f.setAccessible(true);
			String typeName = f.getType().getName();
			if ("java.lang.String".equals(typeName)){
				f.set(record,item.getString(f.getName()));
			}
			else if("java.util.Date".equals(typeName)){
				Date date = new Date(item.getLong(f.getName()));
				f.set(record, date); 
			}
			else if("java.lang.Integer".equals(typeName))
				f.set(record, item.getInt(f.getName()));
		}
		return record;
	}

	public void test() {
		Table table = dynamoDB.getTable("CdrIb");
		Item item = new Item();
		item.withPrimaryKey("enterpriseId", 6000001, "uniqueId", "uuuuuunnnnnnqqqqqqququuuuuueeeseeeeiiiiiiiidddddddd");
		item.withLong("startTime", 1111111111111L)
				.withLong("endTime", 22222222222L)
				.withString("callType", "1")
				.withString("mainUniqueId", "mmmmmmmmaaaaaaiiiiinnnnnn");
		PutItemOutcome putItemOutcome = table.putItem(item);

		System.out.println(putItemOutcome.toString());
	}

	public static void main(String[] args) {
		new DynamoDBTest<>().test();
	}
}
