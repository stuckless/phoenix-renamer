package sagex.phoenix.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import sagex.stub.StubAPIProxy;
import sagex.stub.StubSageAPI;

public class PropertiesStubAPIProxy implements StubAPIProxy {
	private Logger log = Logger.getLogger(this.getClass());
	
    private boolean debug = false;
    
    protected Properties props = new Properties();
    protected Map<String, Object> vars = new HashMap<String, Object>();
	protected File propFile;
    
	public PropertiesStubAPIProxy() {
		this(null);
	}
	
	public PropertiesStubAPIProxy(File pFile) {
		this.propFile=pFile;
		if (pFile!=null) {
			if (pFile.exists()) {
				try {
					log.info("Loading properties from: " + propFile);
					props.load(new FileInputStream(propFile));
				} catch (Exception e) {
					log.warn("Failed to load properties " + propFile, e);
				}
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					saveProperties();
				}
			});
		}
	}
	
    public void saveProperties() {
    	try {
    		FileOutputStream fos = new FileOutputStream(propFile);
    		props.store(fos, "Standalone Properties");
    		fos.flush();
    		fos.close();
    	} catch (Exception e) {
    		log.warn("failed to save properties: " + propFile);
    	}
    }

	public Object call(String cmd, Object[] args) {
        if ("GetProperty".equals(cmd) || "GetServerProperty".equals(cmd) || "GetClientProperty".equals(cmd)) {
            Object prop = props.getProperty((String)args[0]);
            if (prop==null) {
                prop=args[1];
            }
            if (debug) System.out.printf("GetProperty: %s; Value: %s\n", args[0], prop);
            return prop;
        }
        
        if ("SetProperty".equals(cmd) || "SetServerProperty".equals(cmd) || "SetClientProperty".equals(cmd)) {
        	if (args[1]==null) {
        		props.remove(args[0]);
        	} else {
        		props.put(args[0], args[1]);
        	}
            if (debug) System.out.printf("SetProperty: %s; Value: %s\n", args[0], args[1]);
            return null;
        }
        
        if ("AddGlobalContext".equals(cmd) || "AddStaticContext".equals(cmd)) {
            vars.put((String)args[0], args[1]);
            if (debug) System.out.printf("Add Global/Static Context: %s; Value: %s\n", args[0], args[1]);
            return null;
        }
        
        System.out.println("PropertiesStubAPIProxy: Not Handled: " + cmd);
        return null;
    }
    
    public Properties getProperties() {
        return props;
    }
    
    public File getPropFile() {
    	return propFile;
    }
    
    public void attach(StubSageAPI api) {
        debug = api.isDebugEnabled();
        api.addProxy("GetProperty", this);
        api.addProxy("GetServerProperty", this);
        api.addProxy("GetClientProperty", this);
        api.addProxy("SetProperty", this);
        api.addProxy("SetServerProperty", this);
        api.addProxy("SetClientProperty", this);
        api.addProxy("AddStaticContext", this);
        api.addProxy("AddGlobalContext", this);
    }
}
