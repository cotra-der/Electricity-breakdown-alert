const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Function triggered when power outage status changes
exports.sendPowerStatusNotification = functions.database
  .ref('/power_outages/{sectorId}/status')
  .onUpdate((change, context) => {
    const sectorId = context.params.sectorId;
    const newStatus = change.after.val();
    const previousStatus = change.before.val();
    
    // Only send notification if status actually changed
    if (newStatus === previousStatus) {
      return null;
    }
    
    let notificationTitle, notificationBody;
    
    if (newStatus === 'power_cut') {
      notificationTitle = '⚡ Power Outage Alert';
      notificationBody = `Power outage reported in ${sectorId.replace('_', ' ')}`;
    } else if (newStatus === 'normal' && previousStatus === 'power_cut') {
      notificationTitle = '✅ Power Restored';
      notificationBody = `Power has been restored in ${sectorId.replace('_', ' ')}`;
    } else {
      return null; // No notification for other status changes
    }
    
    const payload = {
      notification: {
        title: notificationTitle,
        body: notificationBody,
        sound: 'default',
        clickAction: 'OPEN_RESIDENT_ACTIVITY'
      }
    };
    
    // Send to everyone subscribed to this sector
    return admin.messaging().sendToTopic(sectorId, payload);
  });

// Store outage history for reporting
exports.recordOutageHistory = functions.database
  .ref('/power_outages/{sectorId}/status')
  .onUpdate((change, context) => {
    const sectorId = context.params.sectorId;
    const newStatus = change.after.val();
    const previousStatus = change.before.val();
    
    // Only record if status actually changed
    if (newStatus === previousStatus) {
      return null;
    }
    
    // Get reference to the parent node to access all fields
    return admin.database().ref(`/power_outages/${sectorId}`).once('value')
      .then(snapshot => {
        const outageData = snapshot.val();
        
        const historyEntry = {
          sectorId: sectorId,
          status: newStatus,
          previousStatus: previousStatus,
          timestamp: outageData.lastUpdated,
          reportedBy: outageData.reportedBy || 'system',
          estimatedRestoration: outageData.estimatedRestoration || ''
        };
        
        // Add to history collection
        return admin.database().ref('/outage_history').push(historyEntry);
      });
  });