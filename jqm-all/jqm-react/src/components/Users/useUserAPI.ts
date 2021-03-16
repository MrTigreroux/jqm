import { useState, useCallback } from "react";
import { useSnackbar } from "notistack";
import APIService from "../../utils/APIService";
import { Role, User } from "./User";

const useUserAPI = () => {
    const { enqueueSnackbar } = useSnackbar();
    const [users, setUsers] = useState<any[] | null>();
    const [roles, setRoles] = useState<Role[] | null>();

    const displayError = useCallback((reason: any) => {
        console.log(reason);
        enqueueSnackbar(
            "An error occured, please contact support support@enioka.com for help.",
            {
                variant: "error",
                persist: true,
            }
        );
    }, [enqueueSnackbar]);

    const displaySuccess = useCallback((message: string) => {
        enqueueSnackbar(
            message,
            {
                variant: "success",
            }
        );
    }, [enqueueSnackbar]);

    const fetchRoles = useCallback(async () => {
        APIService.get("/role")
            .then((response) => {
                setRoles(response);
            })
            .catch(displayError);
    }, [displayError]);


    const fetchUsers = useCallback(async () => {
        APIService.get("/user")
            .then((response) => {
                setUsers(response);
            })
            .catch(displayError);
    }, [displayError]);

    const createUser = useCallback(
        async (newUser: User) => {
            return APIService.post("/user", newUser)
                .then(() => {
                    fetchUsers();
                    displaySuccess(`Successfully created user: ${newUser.login}`);
                })
                .catch(displayError);
        },
        [displayError, displaySuccess, fetchUsers]
    );

    const deleteUsers = useCallback(
        async (userIds: any[]) => {
            return await Promise.all(
                userIds.map((id) => APIService.delete("/user/" + id))
            )
                .then(() => {
                    fetchUsers();
                    displaySuccess(`Successfully deleted user${userIds.length > 1 ? "s" : ""}`);
                })
                .catch(displayError);
        },
        [displayError, displaySuccess, fetchUsers]
    );

    const updateUser = useCallback(
        async (user: User) => {
            return APIService.put("/user/" + user.id, user)
                .then(() => {
                    fetchUsers();
                    displaySuccess(`Successfully updated user ${user.login}`);
                })
                .catch(displayError)
        },
        [displayError, displaySuccess, fetchUsers]
    );

    // TODO: change password

    return { users, roles, fetchUsers, fetchRoles, createUser, updateUser, deleteUsers };
};

export default useUserAPI;
