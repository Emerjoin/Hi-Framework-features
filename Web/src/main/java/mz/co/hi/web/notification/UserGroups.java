package mz.co.hi.web.notification;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class UserGroups {

    private static Map<String,String[]> userGroups = new HashMap<String, String[]>();
    private static Map<String,List<String>> groupMembers = new HashMap<String, List<String>>();

    public static String[] getGroupMembers(String groupName){

        synchronized (groupMembers) {

            if (groupMembers.containsKey(groupName)) {

                List<String> membersList = groupMembers.get(groupName);
                String[] membersArray = new String[membersList.size()];
                membersList.toArray(membersArray);

                return membersArray;

            }

        }

        return null;

    }


    public static void setMembership(String principal, String[] groups){

        if(principal==null||groups==null)
            return;

        if(groups.length==0)
            return;

        if(principal.trim().equals(""))
            return;

        synchronized (userGroups){

            userGroups.put(principal,groups);

        }

        for(String group: groups){

            setGroupMember(group,principal);

        }

    }

    public static void setGroupMember(String groupName,String principal){

        synchronized (groupMembers){

            List<String> membersList = null;
            if(groupMembers.containsKey(groupName))

                membersList = groupMembers.get(groupName);

            else
                membersList = new ArrayList<String>();


            membersList.add(principal);
            groupMembers.put(groupName,membersList);

        }


    }

    public static String[] getGroups(String principal){

        String[] groups = null;
        synchronized (userGroups){


            groups = userGroups.get(principal);

        }

        return groups;
    }

    public static void clearMembership(String principal){

        if(principal==null)
            return;

        if(principal.trim().equals(""))
            return;

        String[] groups = null;

        synchronized (userGroups){

            groups = userGroups.get(principal);

        }

        if(groups!=null){


            synchronized (groupMembers){


                for(String group: groups){

                    List<String> members = groupMembers.get(group);

                    if(members!=null)
                        members.remove(principal);

                    groupMembers.put(group,members);


                }

            }

        }


    }


}
