package edu.cmu.cs.cs214.hw5.framework;

/**
 * This interface defined data plugins.
 * 
 * @author Tao
 *
 */
public interface DataPluginInterface {

	/**
	 * The framework will call this method to get the information of the
	 * user.
	 * 
	 * @param id
	 *                The id of the person. This will be use to identify the
	 *                account. It can be user name/id which depends on the
	 *                social network.
	 * @return Return a person object. Use the constructor of {@link Person}
	 *         to create the objects. The framework will automatically set
	 *         other fields in {@link Person};
	 */
	Person getPerson(String id);

	/**
	 * The framework will call this method to get the name of the plugin.
	 * The name will be shown in GUI and will be use to identify the plugin.
	 * 
	 * @return The name of plugin
	 */
	String getPluginName();

	/**
	 * The framework will call this method to get a instruction for the
	 * source user id inputs. The description will be shown in GUI to help
	 * the user to input correct ID.
	 * 
	 * @return The description of the plugin
	 */
	String getInputDescription();

}
