package com.depeng.feature.zk.common;

import java.util.regex.Pattern;


public class Path {
	private String fullPath=null;
	public Path(String fullPath) {
		if(fullPath==null || fullPath.equals("")){
			throw new RuntimeException("path can not be empty");
		}else if(!fullPath.contains("/")){
			// only a name
			this.fullPath = "/"+fullPath;
		}else if(fullPath.startsWith("/")){
			this.fullPath = fullPath;
		}else{
			throw new RuntimeException("path is invalid");
		}
		ensurePath();
	}
	
	public Path(Path parent,String path) {
		if(parent==null)throw new RuntimeException("parent path is empty");
		if(path.startsWith("/")){
			this.fullPath = path;
		}else{
			this.fullPath = parent.fullPath+"/"+path;
		}
		ensurePath();
	}

	public Path getParent(){
		int index = this.fullPath.lastIndexOf("/");
		if(index>0){
			return new Path(this.fullPath.substring(0,index));
		}
		return null;
	}
	
	public Path joinPath(String path){
		return new Path(this,path);
	}
	
	@Override
	public int hashCode() {
		return fullPath.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj instanceof Path){
			return this.fullPath.equals(((Path)obj).fullPath);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.fullPath;
	}
	
	public String[] getPathLevels(){
		return trimLeadingCharacter(fullPath,'/').split("/");
	}
	
	private static Pattern pathRepair = Pattern.compile("(\\\\|\\/)+");
	
	/**
	 * make sure the path string is correct
	 */
	private void ensurePath(){
		if(fullPath ==null){
			fullPath = "";
		}
		fullPath = pathRepair.matcher(fullPath).replaceAll("/");
		fullPath = trimTrailingCharacter(fullPath,'/');
	}
	
	/**
	 * Trim all occurences of the supplied leading character from the given String.
	 * @param str the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (str == null || str.length() <= 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given String.
	 * @param str the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(String str, char trailingCharacter) {
		if (str == null || str.length() <= 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}
