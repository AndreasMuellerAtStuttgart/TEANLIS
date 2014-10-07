package teanlis;

/**
 *
 * This class represents an arbitrary annotation in a text. An annotation has the fields and
 * methods the Linguistic_Element class has, but additionally contains information related
 * to the use of annotations in an active learning framework and the visual representation
 * of the annotation in a text. An annotation contains the following additional fields and
 * methods:
 * <br>
 * <br>
 * 1. The type: The type is meant to denote the general type of the annotation. Note that we
 * distinguish a general type and a more fine-grained type (represented by the field sub-type).
 * This distinction was made because we need to define the general type of entity a user
 * is annotating as well as distinguish those entities into different subtypes for 
 * classification.
 * <br>
 * <br>
 * 2. The sub_type: The sub_type is the actual class of an annotation. For example, a 
 * type could be something general like "text between quotation marks", and the sub_type could
 * then be the function of the text between quotation marks. Possible functions can be that 
 * the text between quotation marks is a title, a quotation or a text the author simply
 * wants to emphasize. When it comes to training a classifier to recognize new instances,
 * usually the sub_type will be used to define of what particular type an entity is, not
 * the type.
 * <br>
 * <br>
 * 3. The attribute unchecked: This attribute expresses whether the classification of an 
 * instance has been made or reviewed by a user. This attribute is useful for active 
 * learning because it lets the classifier know whether the classification made
 * by an authomatic classifier was reviewed by a user and can therefore be viewed as being
 * correct without a doubt, or if it is just an automatic classification and therefore has to
 * be treated differently in the active learning process (for example, an unchecked instance
 * should not be weighed as high as a checked instance when training a new classifier).
 * <br>
 * <br>
 * 4. The attribute probability: This attribute contains the probability a classifier assigns
 * to this annotation having the assigned sub_type. It can be perceived as an estimate of how 
 * sure the classifier is that the sub_type assigned to the annotation is correct. This estimate
 * is important for querying and for computing the contribution of an instance to the training
 * of a new classifier.
 * <br>
 * <br>
 * 5. The attribute unchanged: This attribute stores whether a user changed the classification
 * of an instance or not. This can be important to asses the improvement of quality of a 
 * classifier from one re-training step to the next.
 * 
 * @author Andreas Müller
 */

public class Annotation extends Linguistic_Element implements java.io.Serializable {
	
	/**
	 * Get the type of the annotation.
	 * @return The type of the annotation.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Set the type of the annotation.
	 * @param type The type of the annotation.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see epoetics.core.Linguistic_Element#getString(java.lang.String)
	 */
	public String getString(String original_text) {
		return original_text.substring(start, end);
	}
	
	/**
	 * The color for representing an annotation in a tool.
	 */
	String color;
	
	/**
	 * Get the color of an annotation type for visual representation. Must be set 
	 * first, it's not set in the constructor.
	 * 
	 * @return String representation of the color.
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * Set the color for representing the annotation in a visual interface.
	 * @param color The color for representing the annotation in a visual interface.
	 */
	public void setColor(String color) {
		this.color = color;
	}
	
	/**
	 * The main type of the annotation.
	 */
	String type;
	
	/**
	 * The sub-type of the annotation. For example, a main type could be something
	 * like "text in quotation marks". The sub-type would then something like
	 * "emphasis" or "quotation".
	 */
	String sub_type;
	
	/**
	 * A note made by a user.
	 */
	String note;
	
	/**
	 * Status of the annotation with respect to whether its type was checked by a 
	 * user. True means that it has not been checked, false means that is has been 
	 * checked and its type has been verified as the correct type for this 
	 * annotation.
	 */
	boolean unchecked;
	
	/**
	 * Probability of the classification of an automatically classified instance to 
	 * be correct.
	 */
	double propability;
	
	
	/**
	 * Get the probability that an automatic classification of the instance is  
	 * correct.
	 * @return The probability that an automatic classification of the instance is 
	 * correct.
	 */
	public double getPropability() {
		return propability;
	}
	
	/**
	 * Set the probability that the type (or sub-type, depending on the application) 
	 * is correct.
	 * @param propability The probability that the type (or sub-type, depending on 
	 * the application) is correct.
	 */
	public void setPropability(double propability) {
		this.propability = propability;
	}
	
	/**
	 * Get the status of the instance with respect to whether its type has been 
	 * checked by a user or not.
	 * @return The status of the instance with respect to whether its type has been 
	 * checked by a user or not.
	 */
	public boolean isUnchecked() {
		return unchecked;
	}
	
	/**
	 * Sets the status of the instance with respect to whether its type has been 
	 * checked by a user or not. This method needs the document in which the 
	 * annotation occurs so the status change is recorded in the change log of the 
	 * document.
	 * @param unchecked The status of the instance with respect to whether its type 
	 * has been checked by a user or not.
	 * @param document The document in which the annotation occurs.
	 */
	public void setUnchecked(boolean unchecked, Document document) {
		this.unchecked = unchecked;
		if (this.unchecked) {
			UpdateChangeLog (document, "unchecked");
		} else {
			UpdateChangeLog (document, "checked");
		}
	}
	
	/**
	 * Return the status of the instance with respect to whether a user has changed 
	 * its type or sub-type or not.
	 * @return The status of the instance with respect to whether a user has changed
	 * its type or sub-type or not.
	 */
	public boolean isUnchanged() {
		return unchanged;
	}
	
	/**
	 * Get the last note made by the user attached to this instance.
	 * @return The last note made by the user attached to this instance.
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * Set the last note made by a user for this instance.
	 * @param note The last note made by a user for this instance.
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * Set the status of the annotation with respect to whether its type or sub-type
	 * has been changed by a user or not. This method needs the document the 
	 * annotation occurs in to record the change in the status in the change log of 
	 * the document.
	 * @param unchanged The status of the annotation with respect to whether its 
	 * type or sub-type has been changed by a user or not.
	 * @param document The document the annotation occurs in.
	 */
	public void setUnchanged(boolean unchanged, Document document) {
		this.unchanged = unchanged;
		if (this.unchanged) {
			UpdateChangeLog (document, "unchanged");
		} else {
			UpdateChangeLog (document, "changed");
		}
	}
	
	/**
	 * Status of the annotation with respect to whether its type or sub-type has 
	 * been changed by a user.
	 */
	boolean unchanged;
	
	/**
	 * Get the sub-type of the annotation.
	 * @return The sub-type of the annotation.
	 */
	public String getSub_type() {
		return sub_type;
	}
	
	/**
	 * Set the sub-type of the annotation. This method needs the document the 
	 * annotation is contained in to record the change of the sub-type in the 
	 * change log of the document.
	 * @param sub_type The sub-type of the annotation.
	 * @param document The document the annotation occurs in.
	 */
	public void setSub_type(String sub_type, Document document) {
		this.sub_type = sub_type;
		UpdateChangeLog(document, sub_type);
	}
	
	/**
	 * Constructor for the annotation. Sets the type and sub-type to "unknown", 
	 * unchanged and unchecked to true and the probability to 1.0d (because we are 
	 * certain that those are the correct initial values).
	 */
	public Annotation () {
		this.type = "unknown";
		this.sub_type = "unknown";
		this.unchanged = true;
		this.unchecked = true;
		this.propability = 1.0d;
	}
	
	/**
	 * Constructor for the annotation. The only difference to the no-arguments 
	 * constructor is that the type of the annotation is provided as an argument to 
	 * the constructor.
	 * @param type The type of the annotation.
	 */
	public Annotation (String type) {
		this.type = type;
		this.sub_type = "unknown";
		this.unchanged = true;
		this.unchecked = true;
		this.propability = 1.0d;
	}
	
	/**
	 * Record changes in the annotation for purposes of monitoring, for example,
	 * an active learning process.
	 */
	public void UpdateChangeLog (Document document, String status) {
		document.getInstance_change_log().add("("+this.start+","+this.end+","+status);
	}
}
