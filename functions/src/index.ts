import * as admin from "firebase-admin";
import { onDocumentCreated } from "firebase-functions/v2/firestore";

admin.initializeApp();

const db = admin.firestore();

async function sendPushNotification(
  recipientId: string,
  title: string,
  body: string,
  data: { [key: string]: string }
) {
  try {
    const userDoc = await db.collection("users").doc(recipientId).get();
    const fcmToken = userDoc.data()?.fcmToken;

    if (!fcmToken) {
      console.log(`No FCM token for user: ${recipientId}`);
      return;
    }

    const message: admin.messaging.Message = {
      token: fcmToken,
      notification: { title, body },
      data,
      android: {
        priority: "high",
        notification: {
          sound: "default",
        },
      },
    };

    await admin.messaging().send(message);
    console.log(`✅ Push sent to ${recipientId}`);
  } catch (error) {
    console.error(`❌ Failed to send push: ${error}`);
  }
}

export const onNewNotification = onDocumentCreated(
  "notifications/{notificationId}",
  async (event) => {
    const notification = event.data?.data();
    if (!notification) return;

    const recipientId = notification.recipientId;
    const senderName = notification.sender?.displayName || "Someone";
    const type = notification.type;
    const postId = notification.postId || "";

    let title = "Pulse";
    let body = "";

    switch (type) {
      case "LIKE":
        title = "New Like ❤️";
        body = `${senderName} liked your post`;
        break;
      case "COMMENT":
        title = "New Comment 💬";
        body = `${senderName} commented on your post`;
        break;
      case "FOLLOW":
        title = "New Follower 👤";
        body = `${senderName} started following you`;
        break;
      case "REPOST":
        title = "New Repost 🔁";
        body = `${senderName} reposted your post`;
        break;
      case "MENTION":
        title = "New Mention 📢";
        body = `${senderName} mentioned you`;
        break;
      default:
        body = notification.message || "You have a new notification";
    }

    await sendPushNotification(recipientId, title, body, {
      type,
      postId,
      senderId: notification.senderId || "",
      notificationId: event.params.notificationId,
    });
  }
);