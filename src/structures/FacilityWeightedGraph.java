package structures;

import java.util.ArrayList;

import modules.Facility;

public class FacilityWeightedGraph {
	private ArrayList<Facility> verticies;
    
    /**
     * Creates graph
     * @param facilities - The facilities that make up the nodes
     */
	public FacilityWeightedGraph(ArrayList<Facility> facilities) {
		verticies = facilities;
	}
    
    /**
     * Get a specific facility in the graph
     * @param i - The vertex corresponding to the facility
     * @return The facility corresponding to the vertex
     */
	public Facility get(int i) {
		return verticies.get(i);
	}
    
    /**
     * Gets the n-amount of closest restaurants around an initial location
     * @param src - The initial location
     * @param amount - The amount of restaurants to find
     * @return the n-amount of closest restaurants around an initial location
     */
	public ArrayList<Facility> getClosest(int src, int amount) {
		int[] r = new int[amount];
		double[] distances = new double[amount];

		for (int i = 0; i < r.length; i++) {
			r[i] = -1;
			distances[i] = Double.POSITIVE_INFINITY;
		}

		// Perform single layer BFS;

		// Complete graph contains all edges ...
		for (int i = 0; i < verticies.size(); i++) {
			// except itself
			if (i != src) {
				double dist = getWeight(i, src);

				for (int j = 0; j < amount; j++) {
					if (r[j] == -1 || distances[j] > dist) {
						distances[j] = dist;
						r[j] = i;
						break;
					}
				}
			}
		}

		ArrayList<Facility> facs = new ArrayList<Facility>();

		for (int index : r)
			facs.add(verticies.get(index));

		return facs;
	}
    
    /**
     * Calculates distance between 2 facilities
     * @param lat1 - Latitude of first facility
     * @param lat2 - latitude of 2nd facility
     * @param lon1 - Longitude of 1st facility
     * @param lon2 - Longitude of 2nd facility
     */
	private double distance(double lat1, double lat2, double lon1, double lon2) {
		double R = 6371; // Radius of the earth in km
		double dLat = Math.toRadians(lat2 - lat1); // deg2rad below
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c; // Distance in km
		return d;
	}
    
    /**
     * Gets the weight of an edge between 2 restaurants (weight corresponds to distance)
     * @param a - The first facility
     * @param b - The second facility
     * @return - The weight(distance) of the 2 facilities
     */
	public double getWeight(int a, int b) {
		return distance(verticies.get(a).getLatitude(), verticies.get(b)
				.getLatitude(), verticies.get(a).getLongitude(),
				verticies.get(b).getLongitude());
	}
}
