package org.pureart.flexworks.plugin.ide;

import org.apache.maven.plugin.ide.IdeDependency;
import org.codehaus.plexus.util.StringUtils;
import org.pureart.flexworks.plugin.ide.sdk.LinkType;
import org.pureart.flexworks.plugin.ide.sdk.LocalSdkEntry;
import org.sonatype.flexmojos.plugin.common.FlexScopes;

/**
 * Extends IdeDependency to add the scope value. Additionally this extension
 * handles resolving the path to the dependency to fix a problem that was
 * encountered with 64bit Windows.
 * 
 * @author Lance Linder llinder@gmail.com
 */
public class FbIdeDependency extends IdeDependency {

	private String path = null;

	private String sourcePath = null;

	private String rslUrlTemplate = null;

	private String policyFileUrlTemplate = null;

	private LocalSdkEntry localSdkEntry = null;

	/**
	 * Constructor that copies values from an existing IdeDependency instance
	 * and addes the scope value
	 * 
	 * @param dep
	 * @param scope
	 */
	public FbIdeDependency(IdeDependency dep, String scope) {
		this(dep, scope, null);
	}

	public FbIdeDependency(IdeDependency dep, String scope, LocalSdkEntry entry) {
		this.setArtifactId(dep.getArtifactId());
		this.setClassifier(dep.getClassifier());
		this.setFile(dep.getFile());
		this.setGroupId(dep.getGroupId());
		this.setScope(scope);
		this.setType(dep.getType());
		this.setVersion(dep.getVersion());
		this.setEclipseProjectName(dep.getEclipseProjectName());
		this.setAddedToClasspath(dep.isAddedToClasspath());
		if (dep.getSourceAttachment() != null)
			this.setSourceAttachment(dep.getSourceAttachment()
					.getAbsoluteFile());
		this.setReferencedProject(dep.isReferencedProject());
		this.localSdkEntry = entry;
	}

	/**
	 * dependency scope
	 */
	private String scope;

	/**
	 * Getter for <code>scope</code>.
	 * 
	 * @return Returns the scope.
	 */
	public String getScope() {
		if (this.scope == null)
			return "compile";
		return this.scope;
	}

	/**
	 * Setter for <code>scope</code>.
	 * 
	 * @param scope
	 *            The scope to set.
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setLocalSdkEntry(LocalSdkEntry entry) {
		localSdkEntry = entry;
	}

	public LocalSdkEntry getLocalSdkEntry() {
		return localSdkEntry;
	}

	public Integer getLinkTypeId() {
		return getLinkType().getId();
	}

	public LinkType getLinkType() {
		LinkType defaultType = null;

		switch (FlexBuilderMojo.CURRENT_PROJECT_TYPE) {
		case ACTIONSCRIPT:
			defaultType = LinkType.MERGE;
			break;
		case AIR:
		case FLEX:
			defaultType = LinkType.RSL;
			break;
		case AIR_LIBRARY:
		case FLEX_LIBRARY:
			defaultType = LinkType.EXTERNAL;
			break;
		}

		LinkType type = defaultType;

		if (getScope() == null)
			return type;

		if (getScope().equals(FlexScopes.EXTERNAL)
				|| getScope().equals("runtime")) {
			type = LinkType.EXTERNAL;
		} else if (getScope().equals(FlexScopes.RSL)) {
			type = LinkType.RSL;
		} else if (getScope().equals(FlexScopes.CACHING)) {
			type = LinkType.RSL_DIGEST;
		} else {
			type = LinkType.MERGE; // MERGED is 1. MERGED is default.
		}

		// System.out.println(getArtifactId() + " Scope in " + getScope() +
		// " and link with " + type);

		return type;
	}

	public String getPolicyFileUrl() {
		String url = policyFileUrlTemplate;

		url = StringUtils.replace(url, "{groupId}", getGroupId());
		url = StringUtils.replace(url, "{artifactId}", getArtifactId());
		url = StringUtils.replace(url, "{version}", getVersion());

		return url;
	}

	public void setPolicyFileUrl(String template) {
		policyFileUrlTemplate = template;
	}

	public String getRslUrl(String extension) {
		String url = rslUrlTemplate;
		url = StringUtils.replace(url, "{groupId}", getGroupId());
		url = StringUtils.replace(url, "{artifactId}", getArtifactId());
		url = StringUtils.replace(url, "{version}", getVersion());
		url = StringUtils.replace(url, "{extension}", extension);

		return url;
	}

	public void setRslUrl(String template) {
		rslUrlTemplate = template;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		if (localSdkEntry != null) {
			return localSdkEntry.getPath();
		} else if (path == null) {
			path = this.getFile().getAbsolutePath();
		}
		return path;
	}

	public void setSourcePath(String path) {
		this.sourcePath = path;
	}

	public String getSourcePath() {
		if (localSdkEntry != null) {
			return localSdkEntry.getSourcePath();
		}

		return sourcePath;
	}

	public boolean isFlexSdkDependency() {
		return (localSdkEntry != null || ("rb.swc".equals(getType()) && "com.adobe.flex.framework"
				.equals(getGroupId())));
	}

	public boolean isModifiedFlexSdkDependency() {
		boolean modified = false;

		if (isFlexSdkDependency()) {
			// TODO resolve if this dependency is different from the base.
		}

		return modified;
	}

}
