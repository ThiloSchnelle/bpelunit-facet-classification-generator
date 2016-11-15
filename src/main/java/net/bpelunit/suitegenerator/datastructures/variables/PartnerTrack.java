package net.bpelunit.suitegenerator.datastructures.variables;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import net.bpelunit.suitegenerator.config.Config;

public class PartnerTrack extends BaseVariable {

	private List<String> sequenceOfSlots = new LinkedList<>();
	private TrackType trackType = TrackType.PARTNER;

	private PartnerTrack(String name, Element content) {
		super(name, content);
		readContent(content);
	}

	public static PartnerTrack createPartnerTrack(Element content) {
		String name = null;
		TrackType trackType = TrackType.PARTNER;
		Attribute typeAtt = content.getAttribute(Config.get().getTrackTypeTag());
		if (typeAtt != null) {
			String type = typeAtt.getValue();
			if ("normal".equals(type)) {
				trackType = TrackType.PARTNER;
			} else if ("human".equals(type)) {
				trackType = TrackType.HUMANTASK;
			} else if ("client".equals(type)) {
				trackType = TrackType.CLIENT;
			}
		}
		Attribute nameAtt = content.getAttribute("name");
		if (nameAtt != null) {
			name = nameAtt.getValue();
		}
		if (name == null || name.isEmpty()) {
			Config.get().out().partnerTrackWithoutName(content);
			return null;
		}
		PartnerTrack p = new PartnerTrack(name, content);
		p.trackType = trackType;
		p.readContent(content);
		return p;
	}

	private void readContent(Element content) {
		for (Element child : content.getChildren()) {
			if (child.getName().equals(Config.get().getMessageSlotName())) {
				sequenceOfSlots.add(child.getText());
			}
		}
	}

	public PartnerTrackInstance createPartnerTrackInstance() {
		PartnerTrackInstance pti = new PartnerTrackInstance(name, trackType, new LinkedList<String>(sequenceOfSlots));
		return pti;
	}

	public boolean hasSlot(String slotName) {
		return sequenceOfSlots.contains(slotName);
	}

}
