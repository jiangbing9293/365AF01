package freelancer.worldvideo.alarmset.task;
/**
 * 报警任务 时间段
 * @author jiangbing
 *
 */
public class AlarmTask {

	public long _id;
	public String dev_uid;
	public String time_;
	public String weeks;
	public int flag;
	public int delete;
	/**
	 * 
	 * @param _id
	 * @param dev_uid
	 * @param time_
	 * @param weeks
	 * @param flag 
	 */
	public AlarmTask(long _id, String dev_uid, String time_,
			String weeks, int flag) {
		this._id = _id;
		this.dev_uid = dev_uid;
		this.time_ = time_;
		this.weeks = weeks;
		this.flag = flag;
		this.delete = 0;
	}

	public void setTime_(String time_) {
		this.time_ = time_;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
