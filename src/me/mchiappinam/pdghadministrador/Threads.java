package me.mchiappinam.pdghadministrador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.entity.Player;

public class Threads extends Thread {
	private Main plugin;
	private String tipo;
	
	public Threads(Main pl, String tipo2) {
		plugin=pl;
		tipo=tipo2;
	}
    
    public static String calendario() {
		Calendar agora = Calendar.getInstance();
		SimpleDateFormat gdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss.SSS");
        return gdf.format(agora.getTime());
    }
	
	public void run() {
		switch(tipo) {
			case "all": {
				try {
		        	Connection con = DriverManager.getConnection(Main.mysql_url,Main.mysql_user,Main.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT * FROM `sendcmd` WHERE( `servidor`='"+Main.servidor.trim()+"');");
					ResultSet rs = pst.executeQuery();
		            while(rs.next()) {
		            	String cmd = rs.getString("cmd");
		            	String staffer = rs.getString("staffer");
		            	PreparedStatement pst3 = con.prepareStatement("INSERT INTO log(servidor, staffer, log, data) VALUES(?, ?, ?, ?)");
						//Values
						pst3.setString(1, Main.servidor.trim());
						pst3.setString(2, staffer);
						pst3.setString(3, cmd);
						pst3.setString(4, calendario().trim());
						//Do the MySQL query
						pst3.executeUpdate();
						pst3.close();
						String args[] = cmd.trim().split(" ");
						PreparedStatement pst2 = con.prepareStatement("DELETE FROM `sendcmd` WHERE( `servidor`='"+Main.servidor.trim()+"');");
						pst2.executeUpdate();
						pst2.close();
		            	if(cmd.trim().equalsIgnoreCase("ativarwhitelist")) {
		            		plugin.getServer().broadcastMessage("§3§l[STAFF-"+staffer+"] §aAtivando whitelist...");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "save-all");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload SimpleNoRelog");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload PDGHX1");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload PDGHX1C");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload PDGHCreativoArenas");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload PDGHFullPvPArenas");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugin reload PDGHEventos");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "ativarwhitelist");
		            		//plugin.kickarJogadores();
		            	}else if(cmd.trim().equalsIgnoreCase("desativarwhitelist")) {
		            		plugin.getServer().broadcastMessage("§3§l[STAFF-"+staffer+"] §aDesativando whitelist...");
		            		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "desativarwhitelist");
		            	}else if(args[0].equalsIgnoreCase("msg")) {
		            		plugin.getServer().broadcastMessage(" ");
		            		plugin.getServer().broadcastMessage(" ");
		            		plugin.getServer().broadcastMessage("§d[PDGH] "+cmd.replaceFirst(args[0], "").replaceAll("&", "§").trim());
		            		plugin.getServer().broadcastMessage(" ");
		            		plugin.getServer().broadcastMessage(" ");
		            	}else if(args[0].equalsIgnoreCase("stf")) {
		            		for(Player stf : plugin.getServer().getOnlinePlayers())
		            			if(stf.hasPermission("pdgh.coordenador")) {
				            		stf.sendMessage(" ");
				            		stf.sendMessage(" ");
				            		stf.sendMessage("§3§l[CHAT DA STAFF-"+staffer+"] §b"+cmd.replaceFirst(args[0], "").replaceAll("&", "§").trim());
				            		stf.sendMessage(" ");
				            		stf.sendMessage(" ");
		            			}
		            	}else if(args[0].equalsIgnoreCase("daritem")) {
		            		plugin.cmd.add("give "+staffer+" "+args[1]+" "+args[2]);
		            	}else if(cmd.trim().equalsIgnoreCase("reiniciar")) {
		            		plugin.getServer().broadcastMessage("§3§l[STAFF-"+staffer+"] §aIniciando a reinicialização automática do servidor...");
		            		plugin.cmd.add("autos fshutdown");
		            	}else if(cmd.trim().equalsIgnoreCase("freiniciar")) {
		            		plugin.getServer().broadcastMessage("§3§l[STAFF-"+staffer+"] §aIniciando a reinicialização forçada automática do servidor...");
		            		plugin.cmd.add("stop Reiniciando...");
		            	}else{
		            		plugin.cmd.add(cmd.trim());
		            	}
						rs.close();
						pst.close();
						con.close();
						break;
		            }
					rs.close();
					pst.close();
					con.close();
					break;
				}catch (SQLException ex) {
					System.out.print(ex);
		        }
			}
		}
	}
}
