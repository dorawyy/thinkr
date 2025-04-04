import User from '../db/mongo/models/User';
import { UserDTO } from '../interfaces';

class SubscriptionService {
    /**
     * Updates subscriber status and returns the user information with updated status
     */
    public async updateAndGetSubscriberStatus(
        userId: string,
        subscribe: boolean
    ): Promise<UserDTO> {
        const user = await User.findOne({ googleId: userId });
        if (!user) {
            throw new Error('Error: User not found');
        }

        await User.updateOne(
            { googleId: userId },
            {
                subscribed: subscribe,
            }
        );

        return {
            email: user.email,
            name: user.name,
            googleId: user.googleId,
            subscribed: subscribe,
        } as UserDTO;
    }

    /**
     * Updates subscriber status and returns the user information with updated status
     */
    public async getSubscriberStatus(userId: string): Promise<UserDTO> {
        const user = await User.findOne({ googleId: userId });
        if (!user) {
            throw new Error('Error: User not found');
        }

        return {
            email: user.email,
            name: user.name,
            googleId: user.googleId,
            subscribed: user.subscribed,
        } as UserDTO;
    }
}

export default new SubscriptionService();
