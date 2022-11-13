/* Agent Station environment for static and mobile software agents
 * Copyright (C) 2022  Dr Christos Bohoris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * connectina.co.uk/agent-station
 */
package uk.co.connectina.agentstation.local;

import java.time.LocalDateTime;

import uk.co.connectina.agentstation.App;

/**
 * A schedule for starting an Agent.
 *
 * @author Dr Christos Bohoris
 */
public class Schedule {

    public enum RepeatType { HOURLY, DAILY }

    private long sid;
    private long agentSid;
    private String startDate;
    private String startTime;
    private RepeatType repeat;
    private int interval;
    private int occur;

    public Schedule() {

    }

    public Schedule(long agentSid, String startDate, String startTime, RepeatType repeat, int interval,
            int occur) {
        this.agentSid = agentSid;
        this.startDate = startDate;
        this.startTime = startTime;
        this.repeat = repeat;
        this.interval = interval;
        this.occur = occur;
    }

    public LocalDateTime getStartDateTime() {
        // ISO-8601 formatted string. e.g. 2031-12-30T17:25:00
        String text = startDate + "T" + startTime + ":00";

        return LocalDateTime.parse(text, App.DATETIME_FORMATTER);
    }

    public LocalDateTime getNextOccurence() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = getStartDateTime();
        if (!isRecurring()) {
            return getNextOccurenceDate(next, now);
        }

        int occurCount = 1;
        if (repeat == RepeatType.DAILY) {
            while (occurCount < occur && next.isBefore(now)) {
                next = next.plusDays(interval);
                occurCount++;
            }
        } else if (repeat == RepeatType.HOURLY) {
            while (occurCount < occur && next.isBefore(now)) {
                next = next.plusHours(interval);
                occurCount++;
            }
        }

        return getNextOccurenceDate(next, now);
    }

    private LocalDateTime getNextOccurenceDate(LocalDateTime next, LocalDateTime now) {
        
        return next.isAfter(now) ? next : null;
    }

    public boolean isRecurring() {

        return repeat != null;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getAgentSid() {
        return agentSid;
    }

    public void setAgentSid(long agentSid) {
        this.agentSid = agentSid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public RepeatType getRepeat() {
        return repeat;
    }

    public void setRepeat(RepeatType repeat) {
        this.repeat = repeat;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getOccur() {
        return occur;
    }

    public void setOccur(int occur) {
        this.occur = occur;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.agentSid ^ (this.agentSid >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Schedule other = (Schedule) obj;
        return this.agentSid == other.agentSid;
    }

    @Override
    public String toString() {
        return "Schedule{" + "sid=" + sid + ", agentSid=" + agentSid + ", startDate=" + startDate + ", startTime=" + startTime + ", repeat=" + repeat + ", interval=" + interval + ", occur=" + occur + '}';
    }

}
