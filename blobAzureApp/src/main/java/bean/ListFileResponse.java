package bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by antolivish on 06/05/20.
 */
public class ListFileResponse {

	private List<File> files;

	private int totalRegularFiles;

	private int totalFolders;

	private String nextItemIndex;

	private boolean hasMoreItems;

	public List<File> getFiles()
	{
		if( this.files == null )
		{
			this.files = new ArrayList<>();
		}
		return files;
	}

	public void setFiles( List<File> files )
	{

		this.files = files;
	}

	public int getTotalRegularFiles()
	{
		return totalRegularFiles;
	}

	public void setTotalRegularFiles( int totalRegularFiles )
	{
		this.totalRegularFiles = totalRegularFiles;
	}

	public int getTotalFolders()
	{
		return totalFolders;
	}

	public void setTotalFolders( int totalFolders )
	{
		this.totalFolders = totalFolders;
	}

	public String getNextItemIndex()
	{
		return nextItemIndex;
	}

	public void setNextItemIndex( String nextItemIndex )
	{
		this.nextItemIndex = nextItemIndex;
	}

	public boolean isHasMoreItems()
	{
		return hasMoreItems;
	}

	public void setHasMoreItems( boolean hasMoreItems )
	{
		this.hasMoreItems = hasMoreItems;
	}

	public static class File {

		private String name;

		private long size;

		private boolean isFolder;

		private Date lastModified;

		private String absolutePath;

		private String createdBy;

		public File( String name, long size, boolean isFolder, Date lastModified )
		{
			this.name = name;
			this.size = size;
			this.isFolder = isFolder;
			this.lastModified = lastModified;
		}

		public File()
		{
		}

		public String getName()
		{
			return name;
		}

		public void setName( String name )
		{
			this.name = name;
		}

		public long getSize()
		{
			return size;
		}

		public void setSize( long size )
		{
			this.size = size;
		}

		public boolean isFolder()
		{
			return isFolder;
		}

		public void setFolder( boolean folder )
		{
			isFolder = folder;
		}

		public Date getLastModified()
		{
			return lastModified;
		}

		public void setLastModified( Date lastModified )
		{
			this.lastModified = lastModified;
		}

		public String getAbsolutePath()
		{
			return absolutePath;
		}

		public void setAbsolutePath(String absolutePath) {
			this.absolutePath = absolutePath;
		}

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		@Override
		public boolean equals( Object o )
		{
			if( this == o )
				return true;
			if( o == null || getClass() != o.getClass() )
				return false;

			File file = (File) o;

			if( isFolder != file.isFolder )
				return false;
			return name.equals(file.name);
		}

		@Override
		public int hashCode()
		{
			int result = name.hashCode();
			result = 31 * result + (isFolder ? 1 : 0);
			return result;
		}

		@Override
		public String toString()
		{
			return "File{" + "name='" + name + '\'' + ", size=" + size + ", isFolder=" + isFolder + ", lastModified='"
					+ lastModified + '\'' + '}';
		}
	}
}
