/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

/**
 * @author 
 *
 */
public class SimpleTaskList implements TaskList {
	
	private List<Task> tasks;
	private Integer numTaskOnPage;
	
	public SimpleTaskList() {
		tasks = new ArrayList<Task>();
		numTaskOnPage = null;
	}
	
	public SimpleTaskList(List<String> strList) {
		this();
		addAll(strList);
	}
	
	@Override
	public void addAll(List<String> strList) {
		for (String str : strList) {
			tasks.add(new TaskClass(str));
		}
	}
	
	@Override
	public void clear() {
		tasks.clear();
	}
	
	@Override
	public boolean add(Task task) {
		return tasks.add(task);
	}

	@Override
	public void set(int pos, Task task) {
		tasks.set(pos, task);
	}

	@Override
	public void remove(int pos) {
		tasks.remove(pos);
	}

	@Override
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
	
	@Override
	public Integer indexOf(Task task) {
		return tasks.indexOf(task);
	}

	@Override
	public Task get(Integer pos) {
		return tasks.get(pos);
	}
	
	@Override
	public TaskList clone() {
		TaskList clone = new SimpleTaskList(getStringList());
		clone.setNumTaskOnPage(numTaskOnPage);
		return clone;
	}


	@Override
	public List<String> getStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (Task task : tasks) {
			taskStrings.add(task.toString());
		}

		return taskStrings;
	}

	@Override
	public List<String> getNumberedStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (int i = 0; i < size(); i++) {
			taskStrings.add((i + 1) + ". " + tasks.get(i));
		}

		return taskStrings;
	}

	@Override
	public void sort() {
		Collections.sort(tasks, (task1, task2) -> {
			if (task1.isPrioritized() && !task2.isPrioritized()) {
				return -1;
			} else if (!task1.isPrioritized() && task2.isPrioritized()) {
				return 1;
			}

			if (task1.getStartTime() == null && task2.getStartTime() != null) {
				return 1;
			} else if (task1.getStartTime() != null
					&& task2.getStartTime() == null) {
				return -1;
			}

			if (task1.getStartTime() == null && task2.getStartTime() == null) {
				return task1.getDesc().compareTo(task2.getDesc());
			} else {
				Long thisDate = task1.getStartTime().getTime();
				Long taskDate = task2.getStartTime().getTime();

				return thisDate.compareTo(taskDate);
			}

		});
	}

	@Override
	public int size() {
		return tasks.size();
	}

	@Override
	public void setNumTaskOnPage(Integer number) {
		assert number > 0;
		
		numTaskOnPage = number;
	}

	@Override
	public List<String> getPage(Integer pageNum) {
		assert numTaskOnPage != null;
		
		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}
		
		if (pageNum < 0 || pageNum > getTotalPageNum()) {
			throw new IndexOutOfBoundsException("Invalid Page Number");
		}
		
		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (Task task : tasks.subList(from, to)) {
			taskStrings.add(task.toString());
		}
		
		return taskStrings;
	}

	@Override
	public Integer getTotalPageNum() {
		assert numTaskOnPage != null;
		
		Integer totalPage = tasks.size() / numTaskOnPage;
		totalPage += tasks.size() % numTaskOnPage != 0 ? 1 : 0;
		return totalPage;
	}

	@Override
	public List<String> getNumberedPage(Integer pageNum) {
		assert numTaskOnPage != null;
		
		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}
		
		if (pageNum < 0 || pageNum > getTotalPageNum()) {
			throw new IndexOutOfBoundsException("Invalid Page Number");
		}
		
		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (int i = from; i < to; i++) {
			taskStrings.add((i + 1) + ". " + tasks.get(i).toString());
		}
		
		return taskStrings;
	}

	@Override
	public Integer getIndexPageContainTask(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			return 1;
		}
		
		return taskIndex / numTaskOnPage + 1;
	}

	@Override
	public Integer getIndexTaskOnPage(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			return 0;
		}
		
		return taskIndex % numTaskOnPage;
	}

	@Override
	public TaskList getOverdueTasks() {
		int numOfTask = tasks.size();
		Date current = new Date();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = tasks.get(i);
			if (task.getDeadline() != null) {
				if (task.getDeadline().compareTo(current) <= 0) {
					Task withNum = task.clone();
					withNum.setDesc((i + 1) + ". " + withNum.getDesc());
					resultList.add(task);
				}
			}
		}

		return resultList;
	}

	@Override
	public TaskList search(String content) {
		return processSearch(content);
	}
	
	private TaskList processSearch(String content) {

		String desc = "";
		Integer singlePos = 0;
		Integer doublePos = 0;
		singlePos = content.indexOf('\'', 0);
		doublePos = content.indexOf('\"', 0);
		if (singlePos == -1 && doublePos == -1) {
			// return normal case, just single search(content);
			desc = content;
			return simpleSearch(content, this);
		} else {

			String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
			Matcher matcher = Pattern.compile(regex).matcher(content);
			while (matcher.find()) {
				desc += content.substring(matcher.start() + 1,
						matcher.end() - 1) + " ";
			}
			if (desc.length() > 0) {
				desc = desc.substring(0, desc.length() - 1);
				content = content.replaceAll(regex, "");
			}
			// now content is time

			return complexSearch(desc, content, this);
		}
	}
	
	private Date timeParser(String input) {
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		if (dates.size() == 1) {
			return dates.get(0);
		} else {
			return null;
		}
	}

	// search for date and description
	// if the user types in one date only,
	// the software will understand as search for date
	private TaskList complexSearch(String desc, String content,
			TaskList listToSearch) {

		TaskList resultForTime = simpleSearch(content, listToSearch);
		// search for time first

		return simpleSearch(desc, resultForTime);

	}

	private TaskList simpleSearch(String content, TaskList listToSearch) {

		TaskList listToDisplay = null;

		Date date = timeParser(content);
		if (date == null) {
			listToDisplay = searchDesc(content, listToSearch);
		} else {
			String[] para = content.trim().split("\\s+");
			if (para[0].equalsIgnoreCase("by")) {

				listToDisplay = searchByDate(date, listToSearch);
			} else {
				listToDisplay = searchOnDate(date, listToSearch);
			}
		}

		return listToDisplay;
	}

	// search on the exact date
	private TaskList searchOnDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {
				Task newTask=task.clone();
				String newDesc=(i+1)+". "+task.getDesc();
				newTask.setDesc(newDesc);
				
				if (compare(task.getDeadline(), deadline) == 0) {
					resultList.add(newTask);
				}
			}
		}

		return resultList;
	}

	private TaskList searchByDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {
				
				Task newTask=task.clone();
				String newDesc=(i+1)+". "+task.getDesc();
				newTask.setDesc(newDesc);
				
				if (compare(task.getDeadline(), deadline) <= 0) {
					resultList.add(newTask);
				}
			}
		}

		return resultList;
	}

	// return negative if date1 is before date2
	// positive if date1 is after date2
	// 0 if they are the same

	private int compare(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
			return cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		} else if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			return cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		} else {
			return cal1.get(Calendar.DAY_OF_MONTH)
					- cal2.get(Calendar.DAY_OF_MONTH);
		}

	}

	/**
	 * 
	 * @return: the result list of task if the key appears in the list of task,
	 *          return the list of exact search else return the list of
	 *          nearMatch search
	 * @author: Tran Cong Thien
	 */

	private TaskList searchDesc(String keyWord, TaskList listToSearch) {
		
			return nearMatchSearch(keyWord, listToSearch);

	}


	private TaskList nearMatchSearch(String key, TaskList listToSearch) {
		TaskList resultList = new SimpleTaskList();
		int numOfTask = listToSearch.size();
		String[] str = key.trim().split("\\s+");
		int keyLen = str.length;

		ArrayList<Triple> list = new ArrayList<Triple>();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			Pair result = searchScore(key.toLowerCase(), task.getDesc().trim()
					.toLowerCase());
			if (result.getFirst() > keyLen / 2) {
				if (result.getSecond() >= 500 * keyLen) {
					Task newTask=task.clone();
					String newDesc=(i+1)+". "+newTask.getDesc();
					newTask.setDesc(newDesc);
					list.add(new Triple(result.getFirst(), result.getSecond(),
							newTask));
				}
			}
		}

		Collections.sort(list);

		for (int i = list.size() - 1; i >= 0; i--) {
			Task task = list.get(i).getThird();
			resultList.add(task);
		}

		return resultList;
	}

	private Pair searchScore(String keyword, String strToSearch) {
		String[] key = keyword.trim().split("\\s+");
		int strLen = key.length;
		int searchScore = 0;
		int numOfMatch = 0;
		
		boolean[] isMatched=new boolean[strLen];
		int[] matchScore=new int[strLen]; 
		
		for (int i=0;i<strLen;i++){
			isMatched[i]=false;
			matchScore[i]=0;
		}
		
		
		for (int i = 0; i < strLen; i++) {
			if (strToSearch.indexOf(keyword)!=-1){
				if (isMatched[i]==false){
					isMatched[i]=true;
					matchScore[i]=1000;
				}
			} else if (matchScore(key[i], strToSearch) != 0) {
				if (isMatched[i]==false){
					isMatched[i]=true;
					matchScore[i]=matchScore(key[i], strToSearch);
				} else {
					if (matchScore(key[i],strToSearch)> matchScore[i]){
						matchScore[i]=matchScore(key[i],strToSearch);
					}
				}
			}	
		}

		for (int i=0;i<strLen;i++){
			if (isMatched[i]==true){
				numOfMatch++;
			}
			searchScore+=matchScore[i];
		}
		return new Pair(numOfMatch, searchScore);
	}

	// keyword is one word only
	// return maxScore of matching of this keyword in the string
	private int matchScore(String key, String strToSearch) {

		String[] string = strToSearch.trim().split("\\s+");
		int strLen = string.length;
		int maxScore = 0;

		for (int i = 0; i < strLen; i++) {
			int score = approximateMatchScore(key, string[i]);
			if (maxScore < score) {
				maxScore = score;
			}
		}

		return maxScore;
	}

	// Criteria to be matched between 2 words, if the
	// editDistance/lenghOfKeyWord is <0.5
	// the 2 strings are considered approximately matched
	private int approximateMatchScore(String keyword, String string) {
		int editDist = editDistance(string, keyword);
		int lenOfKey = keyword.length();
		if (editDist / lenOfKey < 0.5)
			return 1000 - 1000 * editDist / lenOfKey;
		else
			return 0;

	}

	// the edit Distance score between 2 strings, used for nearMatch Search
	// the lower, the better
	// Tran Cong Thien
	private int editDistance(String sourceString, String destString) {
		int sourceStrLen = sourceString.length();
		int destStrLen = destString.length();

		// sourceString in for vertical axis
		// destString in the horizontal axis
		int[][] editDistance = new int[sourceStrLen + 1][destStrLen + 1];

		for (int i = 1; i <= sourceStrLen; i++) {
			editDistance[i][0] = i;
		}

		for (int j = 1; j <= destStrLen; j++) {
			editDistance[0][j] = j;
		}

		for (int j = 1; j <= destStrLen; j++) {
			for (int i = 1; i <= sourceStrLen; i++) {

				if (sourceString.charAt(i - 1) == destString.charAt(j - 1)) {
					editDistance[i][j] = editDistance[i - 1][j - 1];
				} else {
					editDistance[i][j] = Math.min(editDistance[i - 1][j] + 1,
							Math.min(editDistance[i][j - 1] + 1,
									editDistance[i - 1][j - 1] + 1));
				}
			}
		}

		return editDistance[sourceStrLen][destStrLen];
	}

}
