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

import controller.Task.TaskType;

/**
 * Class for task lists.
 */
//@author A0119381E
public class SimpleTaskList implements TaskList {

	private static final String SPACE = "\\s+";
	private static final String SINGLE_SPACE=" ";
	private static final String SEPARATOR = ". ";
	private static final String STRING_TODAY = "today";
	private static final String STRING_BY = "by";
	private static final String EMPTY_STRING="";
	private static final String SLASH_STRING="/";
	private static final String QUOTATION_MARK_STRING="\"";
	private static final Character QUOTATION_MARK_CHAR='\"';
	private static final Character SLASH_CHAR='/';
	private static final int MAX_SCORE = 1000;
	private static final int NOT_INDEX=-1;
	private static final int MAX_MONTH=12;
	private static final int MAX_DAY=31;
	private static final double HALF=0.5;
	private static final double UNIT=1.0;
	

	private List<Task> tasks;
	private Integer numTaskOnPage;

	/**
	 * Constructs a SimpleTaskList object.
	 */
	public SimpleTaskList() {
		tasks = new ArrayList<Task>();
		numTaskOnPage = null;
	}

	/**
	 * Constructs a SimpleTaskList object using a list of strings.
	 * 
	 * @param strList
	 *            List of strings.
	 */
	public SimpleTaskList(List<String> strList) {
		this();
		addAll(strList);
	}

	/**
	 * Constructs Task objects from the list of strings and adds them into a
	 * task list.
	 * 
	 * @param strList
	 *            List of stringed tasks.
	 */
	@Override
	public void addAll(List<String> strList) {
		for (String str : strList) {
			tasks.add(new TaskClass(str));
		}
	}

	/**
	 * Clears the list.
	 */
	@Override
	public void clear() {
		tasks.clear();
	}

	/**
	 * Adds a Task object into a task list.
	 * 
	 * @param task
	 *            Task object.
	 * @return true if Task object is successfully added into the task list.
	 */
	@Override
	public boolean add(Task task) {
		return tasks.add(task);
	}

	/**
	 * Adds a Task object into a task list at a specific position.
	 * 
	 * @param index
	 *            Specified position to add task.
	 * @param task
	 *            Task object.
	 */
	@Override
	public void add(int index, Task task) {
		tasks.add(index, task);
	}

	/**
	 * Sets a Task object at a specific position.
	 * 
	 * @param pos
	 *            Specified position to set the Task object.
	 * @param task
	 *            Task object.
	 */
	@Override
	public void set(int pos, Task task) {
		tasks.set(pos, task);
	}

	/**
	 * Removes a task from a specific position.
	 * 
	 * @param pos
	 *            Specified position to remove the Task object.
	 */
	@Override
	public void remove(int pos) {
		tasks.remove(pos);
	}

	/**
	 * Checks if the task list is empty.
	 * 
	 * @return True if the task list is empty.
	 */
	@Override
	public boolean isEmpty() {
		return tasks.isEmpty();
	}

	/**
	 * Gets the index of the task in the task list.
	 * 
	 * @return Index of the task in the task list.
	 */
	@Override
	public Integer indexOf(Task task) {
		return tasks.indexOf(task);
	}

	/**
	 * Gets a task based on its position.
	 * 
	 * @param pos
	 *            Position of the task in the task list.
	 * @return Task object at that position in the task list.
	 */
	@Override
	public Task get(Integer pos) {
		return tasks.get(pos);
	}

	/**
	 * Clones the task list of stringed tasks.
	 * 
	 * @return List of tasks.
	 */
	@Override
	public TaskList clone() {
		TaskList clone = new SimpleTaskList(getStringList());
		clone.setNumTaskOnPage(numTaskOnPage);
		return clone;
	}

	/**
	 * Converts list of Task objects to strings.
	 * 
	 * @return List of stringed tasks.
	 */

	//@author A0115194J
	@Override
	public List<String> getStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (Task task : tasks) {
			taskStrings.add(task.toString());
		}

		return taskStrings;
	}

	/**
	 * Generates a numbered stringed list of tasks.
	 * 
	 * @return List of numbered stringed tasks.
	 */
	@Override
	public List<String> getNumberedStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (int i = 0; i < size(); i++) {
			taskStrings.add((i + 1) + SEPARATOR + tasks.get(i));
		}

		return taskStrings;
	}

	/**
	 * Sorts the list of Task objects in the task list according to priority,
	 * date, time and description.
	 */
	//@author A0115584A
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

	/**
	 * Gets the number of tasks in the task list.
	 * 
	 * @return Number of tasks in the task list.
	 */
	@Override
	public int size() {
		return tasks.size();
	}

	/**
	 * Sets the number of tasks on a page.
	 */
	//@author
	@Override
	public void setNumTaskOnPage(Integer number) {
		assert number > 0;

		numTaskOnPage = number;
	}

	/**
	 * Generates a list of stringed tasks on a page.
	 * 
	 * @param pageNum
	 *            Page number of the task list.
	 * @return List of stringed tasks on that page.
	 */
	@Override
	public List<String> getPage(Integer pageNum) {
		assert numTaskOnPage != null;

		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}

		if (pageNum < 0) {
			pageNum = 0;
		}

		if (pageNum > getTotalPageNum()) {
			pageNum = getTotalPageNum();
		}

		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (Task task : tasks.subList(from, to)) {
			taskStrings.add(task.toString());
		}

		return taskStrings;
	}

	/**
	 * Gets the total number of pages the list has.
	 * 
	 * @return Total number of pages.
	 */
	@Override
	public Integer getTotalPageNum() {
		assert numTaskOnPage != null;

		Integer totalPage = tasks.size() / numTaskOnPage;
		totalPage += tasks.size() % numTaskOnPage != 0 ? 1 : 0;
		return totalPage;
	}

	/**
	 * Generates a list of numbered stringed tasks in a page.
	 * 
	 * @param pageNum
	 *            Page number.
	 * @return List of numbered stringed tasks in that page.
	 */
	@Override
	public List<String> getNumberedPage(Integer pageNum) {
		assert numTaskOnPage != null;

		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}

		if (pageNum < 0) {
			pageNum = 0;
		}

		if (pageNum > getTotalPageNum()) {
			pageNum = getTotalPageNum();
		}

		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (int i = from; i < to; i++) {
			taskStrings.add((i + 1) + SEPARATOR + tasks.get(i).toString());
		}

		return taskStrings;
	}

	/**
	 * Gets the page number that contains the task.
	 * 
	 * @param taskIndex
	 *            Position of the task in the task list.
	 * @return Page number of page that contains this task.
	 */
	@Override
	public Integer getIndexPageContainTask(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			return 1;
		}

		return taskIndex / numTaskOnPage + 1;
	}

	/**
	 * Gets the task number of the task on a page.
	 * 
	 * @param taskIndex
	 *            Position of the task in the task list.
	 * @return Task number of the task on a page.
	 */
	@Override
	public Integer getIndexTaskOnPage(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			return 0;
		}

		return taskIndex % numTaskOnPage;
	}

	/**
	 * Gets a list of overdue tasks.
	 * 
	 * @return List of overdue tasks.
	 */
	//@author A0112044B
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
					withNum.setDesc((i + 1) + SEPARATOR + withNum.getDesc());
					resultList.add(withNum);
				}
			}
		}

		return resultList;
	}

	/*
	 * search for the task with no dates
	 */
	//@author A0112044B
	public TaskList getFloatingTasks() {
		int numOfTask = tasks.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = tasks.get(i);
			if (task.getType() == TaskType.FLOATING) {
				Task withNum = task.clone();
				withNum.setDesc((i + 1) + SEPARATOR + withNum.getDesc());
				resultList.add(withNum);
			}
		}

		return resultList;
	}

	/**
	 * Searches for tasks that consist of the keyword entered by user.
	 * 
	 * @param content
	 *            Keyword entered by user.
	 * @return List of search results.
	 */
	//@author A0112044B
	@Override
	public TaskList search(String content) {
		return processSearch(content);
	}

	/**
	 * Processes user input for search.
	 * 
	 * @param content
	 *            User input.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList processSearch(String content) {

		content = content.trim();
		int first = content.indexOf(QUOTATION_MARK_CHAR);
		int second = content.lastIndexOf(QUOTATION_MARK_CHAR);

		if (first == NOT_INDEX || second == NOT_INDEX) {
			assert first == NOT_INDEX || second == NOT_INDEX;
			return simpleSearch(content, this, false);
		} else {
			if (second == content.length() - 1 && first == 0) {
				String desc = content.replaceAll(QUOTATION_MARK_STRING, EMPTY_STRING);
				return simpleSearch(desc, this, true);
			} else {
				String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
				Matcher matcher = Pattern.compile(regex).matcher(content);
				String desc = new String();
				String time = new String();

				while (matcher.find()) {
					desc += content.substring(matcher.start() + 1,
							matcher.end() - 1)
							+ SINGLE_SPACE;
				}
				if (desc.length() > 0) {
					desc = desc.substring(0, desc.length() - 1);
					time = content.replaceAll(regex, EMPTY_STRING);
				}

				// now content is time
				return complexSearch(desc, time, this);
			}
		}
	}
	
	private List<Date> timeParserPeriod(String input) {
		Parser parser = new Parser();

		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}
		
		if (dates.size()==2){
			return dates;
		}else {
			return null;
		}
		
	}
	/**
	 * Parses user input for date and time.
	 * 
	 * @param input
	 *            User input
	 * @return Date object consisting of the date and time user has entered.
	 */
	//@author A0112044B
	private Date timeParser(String input) {
		Parser parser = new Parser();

		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		if (dates.size() == 1) {


			if (input.toLowerCase().indexOf(STRING_TODAY) == NOT_INDEX) {

				Date now = new Date();
				boolean hasDigit = false;
				for (int i = 0; i < input.length(); i++) {
					if (Character.isDigit(input.charAt(i))) {
						hasDigit = true;
					}
				}

				if (!hasDigit && compare(dates.get(0), now) == 0) {
					return null;
				}

			}

			if (input.length() == 1 || input.length() == 2) {
				// can not get a date form these length
				return null;
			}

			String newStr = new String();
			for (int i = 0; i < input.length(); i++) {
				if (input.charAt(i) == SLASH_CHAR
						|| Character.isDigit(input.charAt(i))) {
					newStr = newStr + input.charAt(i);
				}
			}

			if (newStr.length() >= 4) {
				if (newStr.indexOf(SLASH_STRING) == NOT_INDEX)
					return null;
			}

			if (newStr.length() == 5) {
				if (newStr.charAt(2) == SLASH_CHAR) {
					try {
						int mon = Integer.parseInt(newStr.substring(0, 2));
						int date = Integer.parseInt(newStr.substring(3));

						if (mon > MAX_MONTH || date > MAX_DAY)
							return null;
					} catch (NumberFormatException nfe) {
						return null;
					}

				}
			}

			return dates.get(0);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Searches for date and description of tasks. If user input contains only
	 * one date, it will search for date.
	 * 
	 * @param desc
	 *            Description of tasks.
	 * @param content
	 *            Attributes to search for?
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList complexSearch(String desc, String content,
			TaskList listToSearch) {
		TaskList resultForTime = simpleSearch(content, listToSearch, false);
		// search for time first

		return simpleSearch(desc, resultForTime, true);

	}

	/**
	 * Searches for tasks containing the keyword entered by user.
	 * 
	 * @param content
	 *            Keyword entered by user.
	 * @param listToSearch
	 *            Task list to search from.
	 * @param isDesc
	 *            True if keyword entered is the keyword for description.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList simpleSearch(String content, TaskList listToSearch,
			boolean isDesc) {

		if (isDesc == true) {	
			return searchDesc(content, listToSearch);
		} else {
			if (isPeriod(content)){
				List<Date> dates=timeParserPeriod(content);
				Date start=dates.get(0);
				Date end=dates.get(1);
				return searchPeriod(start,end,listToSearch);
			}else if (isDeadline(content)){
				Date deadline=timeParser(content);
				return searchByDate(deadline,listToSearch);
			}else if (timeParser(content)!=null){
				Date date=timeParser(content);
				return searchOnDate(date,listToSearch);
			}else {
				return searchDesc(content,listToSearch);
			}
		
		}
	}

	/**
	 * Searches on the exact date of tasks.
	 * 
	 * @param deadline
	 *            Date to search.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	public TaskList searchOnDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {
				Task newTask = task.clone();
				if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
					String newDesc = (i + 1) + SEPARATOR + newTask.getDesc();
					newTask.setDesc(newDesc);
				}

				if (compare(task.getDeadline(), deadline) == 0) {
					assert compare(task.getDeadline(), deadline) == 0;
					resultList.add(newTask);
				}
			}
		}

		return resultList;
	}

	/**
	 * Searches for tasks that have a deadline by the specified date.
	 * 
	 * @param deadline
	 *            Deadline entered by user.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList searchByDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {
				assert task.getDeadline() != null;
				Task newTask = task.clone();
				if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
					String newDesc = (i + 1) + SEPARATOR + newTask.getDesc();
					newTask.setDesc(newDesc);
				}

				if (compare(task.getDeadline(), deadline) <= 0) {
					resultList.add(newTask);
					assert compare(task.getDeadline(), deadline) <= 0;
				}
			}
		}

		return resultList;
	}

	/**
	 * Search in a period of time
	 * @param start
	 * @param end
	 * @param listToSearch
	 * @return
	 */
	//@author A0112044B
	private TaskList searchPeriod(Date start, Date end, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

	
		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {
				assert task.getDeadline() != null;
				Task newTask = task.clone();
				if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
					String newDesc = (i + 1) + SEPARATOR + newTask.getDesc();
					newTask.setDesc(newDesc);
				}

				if (task.getType() == TaskType.DEADLINE) {
					if (compare(task.getDeadline(), end) <= 0
							&& compare(task.getDeadline(), start) >= 0) {
						resultList.add(newTask);
					}
				} else if (task.getType() == TaskType.TIMED) {
					if (compare(task.getEndTime(), end) <= 0
							&& compare(task.getStartTime(), start) >= 0) {
						resultList.add(newTask);
					}
				}
			}
		}

		return resultList;
	}
	
	/**
	 * check if the content is a period of time
	 * @param content
	 * @return
	 */
	//@author A0112044B
	private boolean isPeriod(String content){
			
		List<Date> dates=timeParserPeriod(content);
		return (dates!=null);
		
	}
	
	/**
	 * check if the content is a deadline
	 * @param content
	 * @return
	 */
	//@author A0112044B
	private boolean isDeadline(String content){
		String[] para=content.trim().split(SPACE);
		
		if (para.length>=1){
			 boolean hasBy=para[0].equalsIgnoreCase(STRING_BY);
			 String newCont=content.replaceAll(STRING_BY,EMPTY_STRING);
			 Date date=timeParser(newCont);
			 return (date!=null) && hasBy;
		}else {
			return false;
		}
	}

	/**
	 * Compares the two Date objects by computing their difference. If date1 is
	 * before date2, return a negative difference, If date1 is after date2,
	 * return a positive difference. If they are the same, return 0.
	 * 
	 * @param date1
	 *            Date object to be compared with.
	 * @param date2
	 *            Date object to be compared with.
	 * @return Difference between the two Date objects.
	 */
	//@author A0112044B
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
	 * Searches for the descriptions of tasks that contain the keyword. If the
	 * keyword appears in the list of tasks, return the list of exact search.
	 * Else, return the list of nearMatch search.
	 * 
	 * @param keyword
	 *            Keyword entered by user.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	public TaskList searchDesc(String keyWord, TaskList listToSearch) {

		TaskList result = exactSearch(keyWord, listToSearch);

		if (result.size() == 0) {

			TaskList resultList = insideSearch(keyWord, listToSearch);
			if (resultList.size() == 0) {
				return nearMatchSearch(keyWord, listToSearch);
			} else {
				return resultList;
			}
		} else {
			return result;
		}
	}

	/**
	 * Generates a list of search results.
	 * 
	 * @param keyword
	 *            Keyword entered by user.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList insideSearch(String keyWord, TaskList listToSearch) {

		TaskList result = new SimpleTaskList();

		int numOfTask = listToSearch.size();

		// will not search for 1 character only
		if (keyWord.length() >= 2) {

			for (int i = 0; i < numOfTask; i++) {
				Task task = listToSearch.get(i);
				if (isInside(keyWord.toLowerCase(), task.getDesc()
						.toLowerCase())) {
					Task newTask = task.clone();

					if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
						String newDesc = (i + 1) + SEPARATOR
								+ newTask.getDesc();
						newTask.setDesc(newDesc);
						result.add(newTask);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Checks if keyword entered by user is inside the stringed task.
	 * 
	 * @param keyWord
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Stringed task to search from.
	 * @return True if stringed task contains the keyword.
	 */
	//@author A0112044B
	private boolean isInside(String keyWord, String strToSearch) {
		String[] para = keyWord.trim().split(SPACE);
		int keyLen = para.length;

		for (int i = 0; i < keyLen; i++) {
			if (!isSubstring(para[i], strToSearch)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if keyword entered by user is a substring of the description of
	 * the stringed task.
	 * 
	 * @param keyWord
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Stringed task to search from.
	 * @return True if the keyword is a substring.
	 */
	//@author A0112044B
	private boolean isSubstring(String keyWord, String strToSearch) {

		String[] para = strToSearch.trim().split(SPACE);
		int strLen = para.length;

		for (int i = 0; i < strLen; i++) {
			if (para[i].indexOf(keyWord) != NOT_INDEX) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Searches for tasks that contain the exact keyword entered by user.
	 * 
	 * @param keyWord
	 *            Keyword entered by user.
	 * @param listToSearch
	 *            List of tasks to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList exactSearch(String keyWord, TaskList listToSearch) {

		TaskList result = new SimpleTaskList();
		int numOfTask = listToSearch.size();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (isExact(keyWord.toLowerCase(), task.getDesc().toLowerCase())) {
				Task newTask = task.clone();

				if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
					String newDesc = (i + 1) + SEPARATOR + newTask.getDesc();
					newTask.setDesc(newDesc);
					result.add(newTask);
				}
			}
		}

		return result;

	}

	/**
	 * Checks if keyword entered by user matches exactly with the description of
	 * the stringed task.
	 * 
	 * @param keyWord
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Stringed task to search from
	 * @return True if there is an exact match.
	 */
	//@author A0112044B
	private boolean isExact(String keyWord, String strToSearch) {
		String[] para = keyWord.trim().split(SPACE);
		int keyLen = para.length;

		for (int i = 0; i < keyLen; i++) {
			if (!isEqual(para[i], strToSearch)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if keyword entered by user is exactly the same as the description
	 * of the stringed task.
	 * 
	 * @param keyWord
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Stringed task to search from.
	 * @return True if there is an exact match.
	 */
	//@author A0112044B
	private boolean isEqual(String keyWord, String strToSearch) {

		String[] para = strToSearch.trim().split(SPACE);
		int strLen = para.length;

		for (int i = 0; i < strLen; i++) {
			if (keyWord.equalsIgnoreCase(para[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Searches for tasks which contain words similar to the keyword entered by
	 * user.
	 * 
	 * @param key
	 *            Keyword entered by user.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	//@author A0112044B
	private TaskList nearMatchSearch(String key, TaskList listToSearch) {

		TaskList resultList = new SimpleTaskList();
		int numOfTask = listToSearch.size();
		String[] str = key.trim().split(SPACE);
		int keyLen = str.length;

		ArrayList<Triple> list = new ArrayList<Triple>();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			Pair result = searchScore(key.toLowerCase(), task.getDesc().trim()
					.toLowerCase());
			if (result.getFirst() > keyLen / 2) {
				if (result.getSecond() >= (MAX_SCORE / 2) * keyLen) {
					assert result.getSecond() <= MAX_SCORE * keyLen;
					// the total score can not exceed MAX_SCORE*keyLen
					Task newTask = task.clone();
					if (newTask.getDesc().indexOf(SEPARATOR) == NOT_INDEX) {
						String newDesc = (i + 1) + SEPARATOR
								+ newTask.getDesc();
						newTask.setDesc(newDesc);
					}
					list.add(new Triple(result.getFirst(), result.getSecond(),
							newTask));
				}
			}
		}

		if (list.size() == 0) {
			return new SimpleTaskList();
		}

		Collections.sort(list);

		for (int i = 0; i < list.size(); i++) {
			Task task = list.get(i).getThird();
			resultList.add(task);
		}

		return resultList;
	}

	/**
	 * Generates the score for each search result.
	 * 
	 * @param keyword
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Description of task to search from.
	 * @return A pair of the search score and the number of matches the
	 *         description has with the keyword.
	 */
	//@author A0112044B
	private Pair searchScore(String keyword, String strToSearch) {
		String[] key = keyword.trim().split(SPACE);
		int strLen = key.length;
		int searchScore = 0;
		int numOfMatch = 0;

		boolean[] isMatched = new boolean[strLen];
		int[] matchScore = new int[strLen];

		for (int i = 0; i < strLen; i++) {
			isMatched[i] = false;
			matchScore[i] = 0;
		}

		for (int i = 0; i < strLen; i++) {
			if (matchScore(key[i], strToSearch) > 0) {
				if (isMatched[i] == false) {
					isMatched[i] = true;
					matchScore[i] = matchScore(key[i], strToSearch);
				} else {
					if (matchScore(key[i], strToSearch) > matchScore[i]) {
						matchScore[i] = matchScore(key[i], strToSearch);
					}
				}
			}
		}

		for (int i = 0; i < strLen; i++) {
			if (isMatched[i] == true) {
				numOfMatch++;
			}
			searchScore += matchScore[i];
		}
		assert numOfMatch <= strLen;
		return new Pair(numOfMatch, searchScore);
	}

	/**
	 * Generates the score for the matching of keyword with the description of
	 * task.
	 * 
	 * @param key
	 *            Keyword entered by user.
	 * @param strToSearch
	 *            Description of task.
	 * @return Score for match.
	 */
	//@author A0112044B
	private int matchScore(String key, String strToSearch) {

		String[] string = strToSearch.trim().split(SPACE);
		int strLen = string.length;
		int maxScore = 0;

		for (int i = 0; i < strLen; i++) {
			int score = approximateMatchScore(key, string[i]);

			if (maxScore < score) {
				maxScore = score;
			}
		}
		// maxScore can not exceed 1000
		assert maxScore <= MAX_SCORE;
		return maxScore;
	}

	/**
	 * Generates the approximate match score. If the
	 * editDistance/lengthOfKeyWord is <0.2, the 2 strings are considered
	 * approximately matched.
	 * 
	 * @param keyword
	 *            Keyword entered by user.
	 * @param string
	 *            Description of task.
	 * @return Score for match.
	 */
	//@author A0112044B
	private int approximateMatchScore(String keyword, String string) {

		int editDist = editDistance(string, keyword);
		int lenOfStr = string.length();
		if ((editDist / (lenOfStr * UNIT)) < HALF)
			return MAX_SCORE
					- (int) Math.floor(MAX_SCORE * editDist / (lenOfStr * UNIT));
		else
			return 0;

	}

	/**
	 * Gets the edit distance score between the 2 strings for nearMatch search.
	 * The lower, the better
	 * 
	 * @param sourceString
	 * @param destString
	 * @return Distance between the 2 strings.
	 */
	//@author A0112044B
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

	/**
	 * Suggest possible word based on key
	 * 
	 * @param key
	 * @return list of words
	 */
	@Override
	public List<String> suggestWord(final String key) {
		List<String> wordList = new ArrayList<>();
		tasks.forEach((task) -> {
			String desc = task.getDesc();
			String[] words = desc.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (words[i].indexOf(key.trim()) == 0) {
					if (!words[i].trim().equalsIgnoreCase(key.trim())) {
						wordList.add(words[i]);
					}
				}
			}
		});
		return wordList;
	}

}
