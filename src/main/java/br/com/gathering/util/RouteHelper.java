package br.com.gathering.util;

public class RouteHelper {

	public static String route(String method, String path, String suffix) {
		return method + " " + path + (suffix != null ? suffix : "");
	}

	public static String route(String method, String path) {
		return route(method, path, "");
	}

	public static String GET(String path, String suffix) { return route("GET", path, suffix); }

	public static String GET(String path) { return GET(path, ""); }

	public static String POST(String path, String suffix) { return route("POST", path, suffix); }

	public static String POST(String path) { return POST(path, ""); }

	public static String PUT(String path, String suffix) { return route("PUT", path, suffix); }

	public static String PUT(String path) { return PUT(path, ""); }

	public static String DELETE(String path, String suffix) { return route("DELETE", path, suffix); }

	public static String DELETE(String path) { return DELETE(path, ""); }

}
