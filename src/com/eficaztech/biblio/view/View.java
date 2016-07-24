package com.eficaztech.biblio.view;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;

import com.eficaztech.biblio.util.Sessao;

public abstract class View {

	private EventListener<Event> eventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {

			// //System.out.println(event);
 
		} 

	};

	@Init
	public void init() {
		if (!estaLogado()) {
			Executions.sendRedirect("/login");
		}

	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		if (estaLogado()) {
			view.getPage().addEventListener(Events.ON_CHANGE, eventListener);

			Selectors.wireComponents(view, this, false);
			afterCompose();
		} else {
			Component c = Executions.getCurrent().getDesktop().getFirstPage()
					.getFellowIfAny("zk_root", true);
			c.setVisible(false);
		}

	}

	private boolean estaLogado() {
		if (Sessao.get(Sessao.USUARIO) == null
				|| Sessao.get(Sessao.USUARIO).toString().isEmpty()) {
			return false;
		}
		return true;
	}

	public abstract void afterCompose();

}
