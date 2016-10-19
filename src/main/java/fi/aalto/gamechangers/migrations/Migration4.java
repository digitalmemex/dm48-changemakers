package fi.aalto.gamechangers.migrations;

import de.deepamehta.core.DeepaMehtaType;
import de.deepamehta.core.Topic;
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
					"dm4.contacts.institution", "fi.aalto.gamechangers.institution.type",
					"dm4.core.many", "dm4.core.one"),
				"dm4.contacts.phone_number#dm4.contacts.phone_entry");
		
		// Adds "from" and "to" date
		dm4.getTopicType("dm4.events.event")
			.addAssocDefBefore(
				mf.newAssociationDefinitionModel("dm4.core.aggregation_def",
					"dm4.events.event", "fi.aalto.gamechangers.event.type",
					"dm4.core.many", "dm4.core.one"),
				"dm4.datetime#dm4.events.from");
        
		// Adds date of birth and date of death
		String personTypeUri = "dm4.contacts.person";
		dm4.getTopicType(personTypeUri)
		.addAssocDefBefore(
			mf.newAssociationDefinitionModel("dm4.core.composition_def", "fi.aalto.gamechangers.date_of_death",
				personTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"),
			"dm4.contacts.phone_number#dm4.contacts.phone_entry");
		
		long dataWsId = wsService.getWorkspace("fi.aalto.gamechangers.types").getId();
		
		groupAssignToWorkspace(dataWsId,
				"fi.aalto.gamechangers.date_of_death",
				"fi.aalto.gamechangers.work.type",
				"fi.aalto.gamechangers.event.type",
				"fi.aalto.gamechangers.action.type",
				"fi.aalto.gamechangers.institution.type",
				"fi.aalto.gamechangers.brand.name",
				"fi.aalto.gamechangers.group.name",
				"fi.aalto.gamechangers.comment.public",
				"fi.aalto.gamechangers.work.label",
				"fi.aalto.gamechangers.action",
				"fi.aalto.gamechangers.work",
				"fi.aalto.gamechangers.brand",
				"fi.aalto.gamechangers.group",
				"fi.aalto.gamechangers.comment",
				"fi.aalto.gamechangers.proposal");
	}
	
	private void groupAssignToWorkspace(long wsId, String... topicTypeUris) {
		for (String uri : topicTypeUris) {
			Topic topic = dm4.getTopicByUri(uri);
			wsService.assignToWorkspace(topic, wsId);
		}
	}
	
	private DeepaMehtaType addFromAndToDate(String topicTypeUri) {
		return dm4.getTopicType(topicTypeUri)
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.composition_def", "dm4.events.from",
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"))
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.composition_def", "dm4.events.to",
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"));
	}
}
