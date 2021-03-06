package controller;

import java.util.List;
import java.util.Date;

/**
 * Interface for SimpleTaskList class.
 */
//@author A0119381E
public interface TaskList {

	/**
	 * Adds a Task object into a task list.
	 * 
	 * @param task
	 *            Task object.
	 * @return true if Task object is successfully added into the task list.
	 */
	boolean add(Task task);

	/**
	 * Adds a Task object into a task list at a specific position.
	 * 
	 * @param index
	 *            Specified position to add task.
	 * @param task
	 *            Task object.
	 */
	void add(int index, Task task);

	/**
	 * Constructs Task objects from the list of strings and adds them into a
	 * task list.
	 * 
	 * @param strList
	 *            List of stringed tasks.
	 */
	void addAll(List<String> strList);

	/**
	 * Sets a Task object at a specific position.
	 * 
	 * @param pos
	 *            Specified position to set the Task object.
	 * @param task
	 *            Task object.
	 */
	void set(int pos, Task task);

	/**
	 * Removes a task from a specific position.
	 * 
	 * @param pos
	 *            Specified position to remove the Task object.
	 */
	void remove(int pos);

	/**
	 * Sorts the list of Task objects in the task list according to priority,
	 * date, time and description.
	 */
	void sort();

	/**
	 * Checks if the task list is empty.
	 * 
	 * @return True if the task list is empty.
	 */
	boolean isEmpty();

	/**
	 * Gets the index of the task in the task list.
	 * 
	 * @return Index of the task in the task list.
	 */
	Integer indexOf(Task task);

	/**
	 * Gets a task based on its position.
	 * 
	 * @param pos
	 *            Position of the task in the task list.
	 * @return Task object at that position in the task list.
	 */
	Task get(Integer pos);

	/**
	 * Gets the number of tasks in the task list.
	 * 
	 * @return Number of tasks in the task list.
	 */
	int size();

	/**
	 * Clones the task list of stringed tasks.
	 * 
	 * @return List of tasks.
	 */
	TaskList clone();

	/**
	 * Converts list of Task objects to strings.
	 * 
	 * @return List of stringed tasks.
	 */
	List<String> getStringList();

	/**
	 * Generates a numbered stringed list of tasks.
	 * 
	 * @return List of numbered stringed tasks.
	 */
	List<String> getNumberedStringList();

	/**
	 * Clears the list.
	 */
	void clear();

	/**
	 * Sets the number of tasks on a page.
	 */
	void setNumTaskOnPage(Integer number);

	/**
	 * Generates a list of stringed tasks on a page.
	 * 
	 * @param pageNum
	 *            Page number of the task list.
	 * @return List of stringed tasks on that page.
	 */
	List<String> getPage(Integer pageNum);

	/**
	 * Generates a list of numbered stringed tasks in a page.
	 * 
	 * @param pageNum
	 *            Page number.
	 * @return List of numbered stringed tasks in that page.
	 */
	List<String> getNumberedPage(Integer pageNum);

	/**
	 * Gets the total number of pages the list has.
	 * 
	 * @return Total number of pages.
	 */
	Integer getTotalPageNum();

	/**
	 * Gets the page number that contains the task.
	 * 
	 * @param taskIndex
	 *            Position of the task in the task list.
	 * @return Page number of page that contains this task.
	 */
	Integer getIndexPageContainTask(Integer taskIndex);

	/**
	 * Gets the task number of the task on a page.
	 * 
	 * @param taskIndex
	 *            Position of the task in the task list.
	 * @return Task number of the task on a page.
	 */
	Integer getIndexTaskOnPage(Integer taskIndex);

	/**
	 * Gets a list of floating tasks.
	 * 
	 * @return List of floating tasks.
	 */
	TaskList getFloatingTasks();

	/**
	 * Gets a list of overdue tasks.
	 * 
	 * @return List of overdue tasks.
	 */
	TaskList getOverdueTasks();

	/**
	 * Searches for tasks that consist of the keyword entered by user.
	 * 
	 * @param content
	 *            Keyword entered by user.
	 * @return List of search results.
	 */
	TaskList search(String content);

	/**
	 * Searches on the exact date of tasks.
	 * 
	 * @param deadline
	 *            Date to search.
	 * @param listToSearch
	 *            Task list to search from.
	 * @return List of search results.
	 */
	TaskList searchOnDate(Date date, TaskList list);

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
	TaskList searchDesc(String key, TaskList list);

	/**
	 * Suggest possible word based on key
	 * 
	 * @param key
	 * @return list of words
	 */
	List<String> suggestWord(String key);
}
