package cn.plaf.dataauth.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.plaf.dataauth.annotation.DataAuth;

/**
 * @Author
 * @Description 包扫描器
 * @CopyRight
 */
public class ClassScanner {
	public Map<Method, DataAuth> methodMap = new HashMap<Method, DataAuth>();
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private FilenameFilter javaClassFilter; // 类文件过滤器,只扫描一级类
	private final String CLASS_FILE_SUFFIX = ".class"; // Java字节码文件后缀
	private final String INNER_CLASS="$";
	private String packPrefix; // 包路径根路劲

	public List<String> classNameList = new ArrayList<String>();

	public ClassScanner() {
		javaClassFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// 排除内部内
				return !name.contains(INNER_CLASS);
			}
		};
	}

	/**
	 * @Title: scanning
	 * @Description 扫描指定包(本地)
	 * @param basePath 包所在的根路径
	 * @param packagePath 目标包路径
	 * @return Integer 被扫描到的类的数量
	 * @throws ClassNotFoundException
	 */
	public Integer scanning(String basePath, String packagePath) throws ClassNotFoundException {
		packPrefix = basePath;

		String packTmp = packagePath.replace('.', '/');
		File dir = new File(basePath, packTmp);

		// 不是文件夹
		if (dir.isDirectory()) {
			scan0(dir);
		}

		return classes.size();
	}
	
	
	
	public Integer scanningPackage(String basePath, String packagePath) throws ClassNotFoundException {
		packPrefix = basePath;

		String packTmp = packagePath.replace('.', '/');
		File dir = new File(basePath, packTmp);

		// 不是文件夹
		if (dir.isDirectory()) {
			scanAbsPath(dir);
		}

		return classes.size();
	}
	
	
	

	/**
	 * @Title: scanning
	 * @Description 扫描指定包, Jar或本地
	 * @param packagePath 包路径
	 * @param recursive   是否扫描子包
	 * @return Integer 类数量
	 */
	public Integer scanning(String packagePath, boolean recursive) {
		Enumeration<URL> dir;
		String filePackPath = packagePath.replace('.', '/');
		try {
			// 得到指定路径中所有的资源文件
			dir = Thread.currentThread().getContextClassLoader().getResources(filePackPath);
			packPrefix = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			if (System.getProperty("file.separator").equals("\\")) {
				packPrefix = packPrefix.substring(1);
			}

			// 遍历资源文件
			while (dir.hasMoreElements()) {
				URL url = dir.nextElement();
				String protocol = url.getProtocol();

				if ("file".equals(protocol)) {
					File file = new File(url.getPath().substring(1));
					scan0(file);
				} else if ("jar".equals(protocol)) {
					scanJ(url, recursive);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return classes.size();
	}

	/**
	 * @Title: scanJ
	 * @Description 扫描Jar包下所有class
	 * @param url       jar-url路径
	 * @param recursive 是否递归遍历子包
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void scanJ(URL url, boolean recursive) throws IOException, ClassNotFoundException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarURLConnection.getJarFile();

		// 遍历Jar包
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) entries.nextElement();
			String fileName = jarEntry.getName();

			if (jarEntry.isDirectory()) {
				if (recursive) {
				}
				continue;
			}

			// .class
			if (fileName.endsWith(CLASS_FILE_SUFFIX)) {
				String className = fileName.substring(0, fileName.indexOf('.')).replace('/', '.');
				classes.put(className, Class.forName(className));
			}

		}
	}

	/**
	 * @Title: scan0
	 * @Description 执行扫描
	 * @param dir Java包文件夹
	 * @throws ClassNotFoundException
	 */
	private void scan0(File dir) throws ClassNotFoundException {
		File[] fs = dir.listFiles(javaClassFilter);
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			String path = f.getAbsolutePath();
			// 跳过其他文件
			if (path.endsWith(CLASS_FILE_SUFFIX)) {
				String className = StringUtil.getPackageByPath(f, packPrefix); // 获取包名
				Class cls = Class.forName(className);
				classes.put(className, cls);
				final Method[] methods = cls.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(DataAuth.class)) {
						methodMap.put(method, method.getAnnotation(DataAuth.class));
					}
				}
			}
		}
	}

	/**
	 * 获取符合条件的文件
	 * 
	 * @param dir
	 * @return
	 * @throws ClassNotFoundException
	 */
	public List<String> scanPackage(File dir) throws ClassNotFoundException {
		File[] fs = dir.listFiles(javaClassFilter);
		List<String> filePath = new ArrayList<String>();
		for (int i = 0; fs != null && i < fs.length; i++) {
			System.out.println(fs[i].getName());
			classNameList.add(fs[i].getName().replace(CLASS_FILE_SUFFIX, ""));
			// 跳过其他文件
			if (fs[i].getAbsolutePath().endsWith(CLASS_FILE_SUFFIX)) {
				filePath.add(fs[i].getAbsolutePath());
			}
		}
		return filePath;
	}
	
	
	public void scanAbsPath(File dir) {
		try {
			List<String> list  = scanPackage(dir);
			URLClassLoader urlClassLoader=       getClassByAbsolutePath(list);
			
			loadClass(urlClassLoader);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 加载类
	 * 
	 * @param absPath
	 * @return
	 */
	public URLClassLoader getClassByAbsolutePath(List<String> absPath) {

		if (absPath == null || absPath.isEmpty()) {
			return null;
		}
		URL[] urls = new URL[absPath.size()];
		int i = 0;
		for (String str : absPath) {
			try {
				urls[i++] = new URL("file:///"+str);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return URLClassLoader.newInstance(urls);
	}

	/**
	 * 
	 * @param urlClassLoader
	 */
	public void loadClass(URLClassLoader urlClassLoader) {
		if (!classNameList.isEmpty()) {
			for (String name : classNameList) {
				try {
					Class cls = urlClassLoader.loadClass(name);
					classes.put(cls.getName(), cls);
					final Method[] methods = cls.getMethods();
					for (Method method : methods) {
						if (method.isAnnotationPresent(DataAuth.class)) {
							methodMap.put(method, method.getAnnotation(DataAuth.class));
						}
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 获取包中所有类
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getClasses() {
		return classes;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		ClassScanner cs = new ClassScanner();
//		int c = cs.scanning("D:/workSpaceForRuoyi/Mybatis_dataAuthHelper/target/test-classes/","cn.plaf.dataauth.mapper");
		int c = cs.scanningPackage("D:/workSpaceForRuoyi/Mybatis_dataAuthHelper/target/test-classes/","cn.plaf.dataauth.mapper");

		
		System.out.println(c);
		System.out.println(cs.methodMap);
		
//    	Class c =   Class.forName("cn.plaf.dataauth.mapper.ArchDao");
//    	
//        
//        System.out.println(c.getCanonicalName());

	}
}
