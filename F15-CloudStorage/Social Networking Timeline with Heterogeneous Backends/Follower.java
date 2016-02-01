package cc.cmu.edu.minisite;

/**
 * class to store information of a follower
 * @author ZhangYC
 *
 */
public class Follower implements Comparable<Follower> {
    private String name;
    private String url;

    public Follower(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getUrl() {
    	return url;
    }

    @Override
    public int compareTo(Follower o) {
    	// for sort follower in a queue later
        if (name.equals("Anthony B") && o.name.equals("Belanova")) {
            System.out.println("Anthony B "+ "Belanova");
            System.out.println(name.compareTo(o.name));
        }

        if (name.compareTo(o.name) != 0) {
            return name.compareTo(o.name);
        } else {
            return url.compareTo(o.url);
        }
    }

    /**
     * for debug
     */
    public String toString() {
        return String.format("%s\t%s", name, url);
    }
}
