package freelancer.worldvideo.videorecord;

import java.util.UUID;

import com.tutk.IOTC.AVIOCTRLDEFs.STimeDay;

public class EventInfo
{
	public static final int EVENT_UNREADED = 0;
	public static final int EVENT_READED = 1;
	public static final int EVENT_NORECORD = 2;

	public int EventType;
	public long Time;
	public STimeDay EventTime;
	public int EventStatus;

	private UUID m_uuid = UUID.randomUUID();

	public String getUUID() {
		return m_uuid.toString();
	}

	public EventInfo(int eventType, long time, int eventStatus) {

		EventType = eventType;
		Time = time;
		EventStatus = eventStatus;
	}

	public EventInfo(int eventType, STimeDay eventTime, int eventStatus) {

		EventType = eventType;
		EventTime = eventTime;
		EventStatus = eventStatus;
	}

}
