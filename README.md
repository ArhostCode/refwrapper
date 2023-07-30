# RefWrapper


<!-- ABOUT THE PROJECT -->
## About The Project
The project is dedicated to the topic of reflection in java. A convenient wrapper is provided for interacting with objects via proxy using reflections.

**When should it be applied?** When an object is loaded using another ClassLoader, the reflections mechanism is used to access the classes. The project solves the difficulties of using reflection in this case.

<!-- GETTING STARTED -->
## Getting Started

1. Go to the release page and download the latest version of the project.
2. Connect it as a jar library using the capabilities of your system to automate the build of applications

<!-- USAGE EXAMPLES -->
## Usage

Let's say we have a sample.jar with the following contents:

```java
//JavaObject
class JavaObject {
    private Listener listener;
    public Listener getListener() {
        return listener;
    }
    private JavaObject() {
        listener = new Listener("sample");
    }
    public static JavaObject getInstance() {
        return new JavaObject();
    }
}
//Listener
class Listener {
    public String name;
    public Listener(String name) {
        this.name = name;
    }
    public Entity createEntity(String name) {
        return new Entity(this.name + name);
    }
}
//Entity
class Entity {
    public UUID uuid;
    public String name;
    public Entity(String name) {
        this.name = name;
        uuid = UUID.randomUUID();
    }
}


```

To connect sample.jar we use:
```java
public class Main {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
        URLClassLoader urlClassLoader = new URLClassLoader("SampleLoader", new URL[]{
                new File("sample.jar").toURI().toURL()
        }, Main.class.getClassLoader());
    }
}
```

To access the classes we use:

```java
@Wrapped
public interface JavaObjectWrapper {

    @Method("getInstance")
    JavaObjectWrapper getInstance();
    
    @Method("getListener")
    ListenerWrapper getListener();
}

@Wrapped
public interface ListenerWrapper {

    @Method("createEntity")
    EntityWrapper createEntity(String name);
}

@Wrapped
public interface EntityWrapper {

    @Field("name")
    String getName();

    @Field(value = "name", isSetter = true)
    void setName(String name);
}

public class Main {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
        URLClassLoader urlClassLoader = new URLClassLoader("sample", new URL[]{
                new File("sample.jar").toURI().toURL()
        }, Main.class.getClassLoader());
        
        // Create static wrapper
        JavaPluginWrapper wrapper = WrapperFactory.createWrapper(JavaObjectWrapper.class, urlClassLoader, "space.ardyc.test.JavaObject");
        
        // Get non-static instance
        JavaPluginWrapper object = wrapper.getInstance();
        
        // Get listener object and call createEntity method
        EntityWrapper entity = object.getListener().createEntity("sample");
        
        System.out.println(entity.getName()); // enabledsamepl
        
        //Set field param
        entity.setName("none");
        System.out.println(entity.getName()); //none
    }
}
```