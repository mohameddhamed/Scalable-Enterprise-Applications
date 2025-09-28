package utils;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


/**
 * Utilities for JNDI (Java Naming and Directory Interface).
 */
public class JndiUtils {
	static boolean wasReflectiveMessageShown = false;

	/**
	 * Looks up the JNDI name {@code jndiName}, and returns the found object.
	 * In our case, it will be an EJB.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String applicationName, String moduleName, String beanName, Class<T> interfaceClass) {
		return withStandardContext(ctx -> {
			String jndiName = makeJndiName(applicationName, moduleName, beanName, interfaceClass);
			return (T)ctx.lookup(jndiName);
		});
	}

	/**
	 * For more information on portable global JNDI names see:
	 * {@link https://blogs.oracle.com/MaheshKannan/entry/portable_global_jndi_names}
	 * @return The portable JNDI name of the bean.
	 */
	public static String makeJndiName(String applicationName, String moduleName, String beanName, Class<?> interfaceClass) {
		String fullyQualifiedBeanInterfaceName = interfaceClass.getCanonicalName();
		return String.format("java:global/%s/%s/%s!%s", applicationName, moduleName, beanName, fullyQualifiedBeanInterfaceName);
	}

	public static List<String> listAvailableBeans() {
		return withStandardContext(ctx -> {
			List<String> retval = new ArrayList<>();
			NamingEnumeration<NameClassPair> list = ctx.list("");
			while (list.hasMore()) {
				retval.add(list.next().getName());
			}
			return retval;
		});
	}

	// --------------------------------------------------------------------

	static Properties createEnvironment() {
		// Explicitly specify the location of the server
		// (these are the default values, so this block is unnecessary)
		Properties environment = new Properties();

//		environment.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		environment.setProperty("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
		environment.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
		return environment;
	}

	@FunctionalInterface
	public interface SupplierExc<T> { T get() throws Exception; }
	public interface FunctionExc<T, T2> { T2 apply(T in) throws Exception; }

	public static <T> T withStandardContext(FunctionExc<Context, T> getData) {
		return withContext(() -> {
			Properties environment = createEnvironment();
			return new InitialContext(environment);
		}, getData);
	}

	public static void printHackMessage() {
		if (wasReflectiveMessageShown) {
			System.out.println("(Happened again.)");
			return;
		}

		System.out.println("The application client just stopped working with an 'Illegal reflective access' message.");
		System.out.println("This indicates a bean lookup failure. Due to this, you'll probably also see a NullPointerException later on.");
		System.out.println("To make it go away, do this.");
		System.out.println("1. Go to Run/Run configurations in the menu");
		System.out.println("2. Add the following into Arguments/VM Arguments:");
		System.out.println("       --add-opens java.base/java.lang=ALL-UNNAMED");
		System.out.println("       --add-opens=java.base/java.io=ALL-UNNAMED");
		System.out.println("       --add-opens=java.management/javax.management=ALL-UNNAMED");
		System.out.println("       --add-opens=java.management/javax.management.openmbean=ALL-UNNAMED");
	}

	// Provides resource handling for context related operations.
	public static <T> T withContext(SupplierExc<Context> getContext, FunctionExc<Context, T> getData) {
		Context ctx = null;
		try {
			ctx = getContext.get();
			return getData.apply(ctx);
		} catch (NamingException ex) {
			if (isCauseReflective(ex)) {
				JndiUtils.printHackMessage();
				wasReflectiveMessageShown = true;
			} else {
				ex.printStackTrace();
			}

			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	private static boolean isCauseReflective(Throwable ex) {
		while (true) {
			if (ex == null)   return false;

			if (ex instanceof InaccessibleObjectException)   return true;
			ex = ex.getCause();
		}
	}

}
