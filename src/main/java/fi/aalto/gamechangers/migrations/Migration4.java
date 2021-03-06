package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;import de.deepamehta.core.DeepaMehtaType;

import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.workspaces.WorkspacesService;

/**
 */
public class Migration4 extends Migration {

	@Inject
	private WorkspacesService wsService;

	/** Modifies:
	 * 
	 */
	@Override
	public void run() {
		// Adds institution type (and "from" and "to" date)
		addFromAndToDate("dm4.contacts.institution")
			.addAssocDefBefore(
				mf.newAssociationDefinitionModel("dm4.core.aggregation_def",
					"dm4.contacts.institution", NS("institution.type"),
					"dm4.core.many", "dm4.core.one"),
				"dm4.contacts.phone_number#dm4.contacts.phone_entry");
		
		// Adds "type of event"
		TopicType eventType = dm4.getTopicType("dm4.events.event");
		eventType.addAssocDefBefore(
			mf.newAssociationDefinitionModel("dm4.core.aggregation_def",
				"dm4.events.event", NS("event.type"),
				"dm4.core.many", "dm4.core.one"),
			"dm4.datetime#dm4.events.from");
		eventType.addAssocDef(
			mf.newAssociationDefinitionModel("dm4.core.aggregation_def",
				"dm4.events.event", NS("event.hidden"),
				"dm4.core.many", "dm4.core.one"));
		eventType.getAssocDef("dm4.datetime#dm4.events.from").setTypeUri("dm4.core.aggregation_def");
		eventType.getAssocDef("dm4.datetime#dm4.events.to").setTypeUri("dm4.core.aggregation_def");
        
		// Adds date of death
		String personTypeUri = "dm4.contacts.person";
		dm4.getTopicType(personTypeUri)
		.addAssocDefBefore(
			mf.newAssociationDefinitionModel("dm4.core.composition_def", NS("date_of_death"), false,
				personTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"),
			"dm4.contacts.phone_number#dm4.contacts.phone_entry");

		// Adds "from" and "to" date for new types
		addFromAndToDate(NS("work"));
		addFromAndToDate(NS("brand"));
		addFromAndToDate(NS("group"));
		addFromAndToDate(NS("proposal"));
		
		// Workspace associations
		long dataWsId = wsService.getWorkspace(NS("workspace.types")).getId();
		
		groupAssignToWorkspace(dataWsId,
				NS("date_of_death"),
				NS("work.type"),
				NS("event.type"),
				NS("action.type"),
				NS("institution.type"),
				NS("brand.name"),
				NS("group.name"),
				NS("comment.public"),
				NS("work.label"),
				NS("action"),
				NS("work"),
				NS("brand"),
				NS("group"),
				NS("comment"),
				NS("proposal"));

		// Assigns all the values for the 'type' topics
		groupAssignToWorkspace(dataWsId, dm4.getTopicsByType(NS("work.type")));
		groupAssignToWorkspace(dataWsId, dm4.getTopicsByType(NS("event.type")));
		groupAssignToWorkspace(dataWsId, dm4.getTopicsByType(NS("action.type")));
		groupAssignToWorkspace(dataWsId, dm4.getTopicsByType(NS("institution.type")));
	}
	
	private void groupAssignToWorkspace(long wsId, String... topicTypeUris) {
		for (String uri : topicTypeUris) {
			Topic topic = dm4.getTopicByUri(uri);
			wsService.assignToWorkspace(topic, wsId);
		}
	}

	private void groupAssignToWorkspace(long wsId, Iterable<Topic> topics) {
		for (Topic topic : topics) {
			wsService.assignToWorkspace(topic, wsId);
		}
	}
	
	private DeepaMehtaType addFromAndToDate(String topicTypeUri) {
		return dm4.getTopicType(topicTypeUri)
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.aggregation_def", "dm4.events.from", false,
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"))
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.aggregation_def", "dm4.events.to", false,
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"));
	}
}
